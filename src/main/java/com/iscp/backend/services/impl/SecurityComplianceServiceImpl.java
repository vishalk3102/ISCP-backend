package com.iscp.backend.services.impl;

import com.iscp.backend.components.*;
import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.*;
import com.iscp.backend.mapper.SecurityComplianceMapper;
import com.iscp.backend.models.*;
import com.iscp.backend.models.Enum;
import com.iscp.backend.repositories.*;
import com.iscp.backend.services.SecurityComplianceService;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link SecurityComplianceService} interface for managing SecurityCompliance.
 */
@Service
@Slf4j
@AllArgsConstructor
public class SecurityComplianceServiceImpl implements SecurityComplianceService {

    private final FrameworkRepository frameworkRepository;

    private final PeriodicityManager periodicityManager;

    private final ControlRepository controlRepository;

    private final ChecklistRepository checklistRepository;

    private final EvidenceRepository evidenceRepository;

    private final DepartmentRepository  departmentRepository;

    private final SecurityComplianceRepository securityComplianceRepository;

    private final SecurityComplianceMapper securityComplianceMapper;

    private  final ExportExcel exportExcel;

    private final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy dd");

    private final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yy-MM");

    private final UsersRepository usersRepository;


    /**
     * Add or update a list of security compliance based on the provided list of security compliance DTO.
     *
     * @param securityComplianceCreateDTOList a list of SecurityComplianceCreateDTO containing details of security compliance to be added or updated.
     * @return a list of {@link SecurityComplianceDTO} containing added or updated security compliance.
     * @throws SecurityComplianceNotFoundException if a security ID is not found in the repository.
     * @throws ChecklistNotFoundException if any of the specified checklist does not exist.
     * @throws FrameworkNotFoundException if the specified framework does not exist.
     * @throws DepartmentNotFoundException if any of the specified department does not exist.
     * @throws ControlNotFoundException if the specified control does not exist.
     * @throws PeriodicityUpdateDeniedException if the periodicity update is denied
     */
    @Override
    public List<SecurityComplianceDTO> addEditSecurityCompliance(List<SecurityComplianceCreateDTO> securityComplianceCreateDTOList) throws SecurityComplianceNotFoundException, ChecklistNotFoundException, FrameworkNotFoundException, DepartmentNotFoundException, ControlNotFoundException, PeriodicityUpdateDeniedException {
        //List to store security compliance which needs to be updated
        List<SecurityComplianceEditDTO> securityComplianceEditDTOS = new ArrayList<>();

        //List to store security compliance which needs to be added
        List<SecurityComplianceCreateDTO> securityComplianceCreateDTOS = new ArrayList<>();

        for(SecurityComplianceCreateDTO securityDTO : securityComplianceCreateDTOList) {
            String securityId = securityDTO.getSecurityId();

            //Check if SecurityId is blank
            if(securityId.isBlank()) {
                //validate and add it to the list to add
                validateSecurityComplianceCreateDTO(securityDTO);
                securityComplianceCreateDTOS.add(securityDTO);
            }
            //If SecurityId is not found, then throw an exception
            else if(securityComplianceRepository.findBySecurityId(securityId).isEmpty()) {
                throw new SecurityComplianceNotFoundException();
            }
            //Validate the checklist and add it to the UpdateList
            else {
                //validate and add it to the list to add
                validateSecurityComplianceCreateDTO(securityDTO);
                SecurityComplianceEditDTO securityEditDTO = securityComplianceMapper.toSecurityComplianceEditDTO(securityDTO);
                securityComplianceEditDTOS.add(securityEditDTO);
            }
        }

        List<SecurityComplianceDTO> securityComplianceDTOListAdded = addSecurityCompliance(securityComplianceCreateDTOS);

        List<SecurityComplianceDTO> securityComplianceDTOListUpdated = editSecurityCompliance(securityComplianceEditDTOS);

        //Combine both added and updated records into a single list and return
        securityComplianceDTOListAdded.addAll(securityComplianceDTOListUpdated);
        return securityComplianceDTOListAdded;
    }


    /**
     * Add a list of security compliance based on the provided list of security compliance DTO.
     *
     * @param securityComplianceCreateDTOList a list of SecurityComplianceCreateDTO containing security compliance details to be added.
     * @return a list of {@link SecurityComplianceDTO} containing added security compliance.
     */
    @Override
    public List<SecurityComplianceDTO> addSecurityCompliance(List<SecurityComplianceCreateDTO> securityComplianceCreateDTOList) {
        log.debug("Attempting to add security Compliance");

        List<SecurityCompliance> securityComplianceList = new ArrayList<>();

        for(SecurityComplianceCreateDTO securityComplianceCreateDTO:securityComplianceCreateDTOList) {

            //Fetch the associated framework, control, checklist and department
            Optional<Framework> framework = frameworkRepository.findByFrameworkName(securityComplianceCreateDTO.getFrameworkName());
            Optional<Control> control =  controlRepository.findByControlName(securityComplianceCreateDTO.getControlName());
            Set<Checklist> checklists = checklistRepository.findByControlChecklistInAndControl_ControlName(securityComplianceCreateDTO.getChecklistName(), securityComplianceCreateDTO.getControlName());
            Set<Department> departments = departmentRepository.findAllByName(securityComplianceCreateDTO.getDepartments());

            //Determine the frequency based on periodicity
            Integer frequency = periodicityManager.getFrequency(securityComplianceCreateDTO.getPeriodicity());

            List<String> eventDateList = securityComplianceCreateDTO.getEventDate();
            String startDate = findStartDate(securityComplianceCreateDTO.getPeriodicity(), framework, eventDateList);

            for(Checklist checklist : checklists) {
                //Check if the security compliance already exists
                SecurityComplianceCheckDTO securityComplianceCheckDTO=securityComplianceMapper.toCheckDTO(securityComplianceCreateDTO);
                Boolean ifExists = isSecurityComplianceAlreadyExists(securityComplianceCheckDTO);
                if(ifExists)
                    continue;

                String complianceId = CustomIdGenerator.generateComplianceId();

                //Generate initial record Id
                String recordId = RecordIdGenerator.generateRecordId(securityComplianceCreateDTO.getPeriodicity(),startDate);
                LocalDateTime now = LocalDateTime.now();

                for(int i =1;i<=frequency;i++) {
                    SecurityCompliance securityCompliance = securityComplianceMapper.toSecurityComplianceEntity(securityComplianceCreateDTO);
                    now = now.minusSeconds(1);
                    //Set the attributes of the security compliance entity
                    securityCompliance.setRecordId(recordId);
                    securityCompliance.setEvidenceComments("pending");
                    securityCompliance.setCreationTime(now);
                    securityCompliance.setComplianceId(complianceId);
                    securityCompliance.setFramework(framework.get());
                    securityCompliance.setControl(control.get());
                    securityCompliance.setDepartments(departments);
                    securityCompliance.setChecklist(checklist);
                    securityComplianceList.add(securityCompliance);
                    //Increment record ID for next record
                    if(securityCompliance.getPeriodicity().equals(Enum.Periodicity.Monthly)) {
                        recordId = RecordIdGenerator.incrementRecordIdMonthly(recordId, i + 1);
                    }
                    else{
                        if(i<frequency) {
                            startDate = securityComplianceCreateDTO.getEventDate().get(i);
                        }
                        recordId = RecordIdGenerator.incrementRecordId(recordId, startDate, i + 1);
                    }
                }
            }
        }
        //save added compliance to the database
        List<SecurityCompliance> securityComplianceCreatedList =  securityComplianceRepository.saveAll(securityComplianceList);
        List<SecurityComplianceDTO> securityCompAddedList = new ArrayList<>();

        for(SecurityCompliance securityCompliance : securityComplianceCreatedList) {
            SecurityComplianceDTO securityCompAdded = securityComplianceMapper.toSecurityComplianceDTO(securityCompliance);
            //Add the compliance to the list
            securityCompAddedList.add(securityCompAdded);
        }
        log.info("security compliance added successfully: {}",securityCompAddedList);

        //Return the list of added security compliance
        return securityCompAddedList;
    }


    /**
     * Validates the SecurityComplianceCreateDTO to ensure all required entities exist.
     *
     * @param securityDTO the SecurityComplianceCreateDTO containing data to validate.
     * @throws FrameworkNotFoundException if the specified framework is not found.
     * @throws ControlNotFoundException if the specified control is not found.
     * @throws ChecklistNotFoundException if the specified checklist is not found.
     * @throws DepartmentNotFoundException if the specified department is not found.
     */
    private void validateSecurityComplianceCreateDTO(SecurityComplianceCreateDTO securityDTO) throws FrameworkNotFoundException, ControlNotFoundException, ChecklistNotFoundException, DepartmentNotFoundException {

        //Validate the framework
        Optional<Framework> framework = frameworkRepository.findByFrameworkName(securityDTO.getFrameworkName());
        if (framework.isEmpty()) {
            log.error("Error adding security compliance: framework not found with name {}", securityDTO.getFrameworkName());
            throw new FrameworkNotFoundException();
        }

        //Validate the control
        Optional<Control> control = controlRepository.findByControlName(securityDTO.getControlName());
        if (control.isEmpty()) {
            log.error("Error adding security compliance: control not found with name {}", securityDTO.getControlName());
            throw new ControlNotFoundException();
        }

        //Validate the checklist
        Set<Checklist> checklists = checklistRepository.findByControlChecklistInAndControl_ControlName(securityDTO.getChecklistName(),securityDTO.getControlName());
        if (checklists.isEmpty()) {
            log.error("Error adding security compliance: checklist not found with name {}", securityDTO.getChecklistName());
            throw new ChecklistNotFoundException();
        }

        //Validate the department
        Set<Department> departments = departmentRepository.findAllByName(securityDTO.getDepartments());
        if (departments.isEmpty()) {
            log.error("Error adding security compliance: department not found with name {}", securityDTO.getDepartments());
            throw new DepartmentNotFoundException();
        }
    }


    /**
     * Update a list of security compliance based on the provided list of security compliance DTO.
     *
     * @param securityComplianceEditDTOList a list of SecurityComplianceEditDTO containing security compliance details to be updated.
     * @return a list of {@link SecurityComplianceDTO} containing updated security compliance.
     * @throws SecurityComplianceNotFoundException if a security ID is not found in the repository.
     * @throws FrameworkNotFoundException if the specified framework does not exist.
     * @throws PeriodicityUpdateDeniedException if the periodicity update is denied
     */
    @Override
    public List<SecurityComplianceDTO> editSecurityCompliance(List<SecurityComplianceEditDTO> securityComplianceEditDTOList) throws SecurityComplianceNotFoundException, FrameworkNotFoundException, PeriodicityUpdateDeniedException {
        log.debug("Attempting to edit {} security compliance", securityComplianceEditDTOList.size());

        //List to store all updated security compliance
        List<SecurityComplianceDTO> updatedComplianceList = new ArrayList<>();
        String recordId;

        for (SecurityComplianceEditDTO securityComplianceEditDTO : securityComplianceEditDTOList) {
            log.info("Compliance is {}", securityComplianceEditDTO);

            //Check if the given security compliance exist in the database
            SecurityCompliance existingCompliance = securityComplianceRepository.findById(securityComplianceEditDTO.getSecurityId())
                    .orElseThrow(SecurityComplianceNotFoundException::new);

            //Fetch the associated checklist, department and framework
            Set<Checklist> checklists = checklistRepository.findByControlChecklistInAndControl_ControlName(securityComplianceEditDTO.getChecklistName(), securityComplianceEditDTO.getControlName());
            Set<Department> departments = departmentRepository.findAllByName(securityComplianceEditDTO.getDepartments());
            Optional<Framework> framework = frameworkRepository.findByFrameworkName(securityComplianceEditDTO.getFrameworkName());

            //Fetch all compliance related to given complianceId
            List<SecurityCompliance> complianceList = securityComplianceRepository.findAllByComplianceId(securityComplianceEditDTO.getComplianceId());
            if (complianceList.isEmpty()) {
                throw new SecurityComplianceNotFoundException();
            }

            //Find the start date for recordId
            List<String> eventDateList = securityComplianceEditDTO.getEventDate();
            String startDate = findStartDate(securityComplianceEditDTO.getPeriodicity(), framework, eventDateList);

            //Determine exiting and new frequency (e.g. 2 - BI_Annually, 4 - Quarterly)
            Integer existingFrequency = findFrequency(securityComplianceEditDTO, complianceList, false);
            Integer newFrequency = findFrequency(securityComplianceEditDTO, complianceList, true);

            //used to increment recordId and traverse eventList
            int frequencyIndex = 2, eventListIndex=1;

            for (Checklist checklist : checklists) {
                //If compliance status is true, then allows to edit
                if (existingCompliance.getEvidenceComplianceStatus()) {

                    //Validate that NewFrequency is greater than or equal to ExistingFrequency
                    if (newFrequency >= existingFrequency) {
                        //Generate initial record Id
                        recordId = RecordIdGenerator.generateRecordId(securityComplianceEditDTO.getPeriodicity(), startDate);
                        LocalDateTime creationTime = LocalDateTime.now();

                        //Update existing compliance records
                        updateExistingComplianceRecords(securityComplianceEditDTO, complianceList, checklist, departments, existingCompliance, recordId, creationTime, newFrequency, existingFrequency, startDate, updatedComplianceList, frequencyIndex, eventListIndex);

                    }
                    else {
                        throw new PeriodicityUpdateDeniedException();
                    }
                }
            }
        }
        //Return list of updated security compliance
        log.debug("Edited All Security Compliance");
        return updatedComplianceList;
    }


    /**
     * Determines the start date for a security compliance based on the periodicity.
     *
     * @param periodicity Enum containing periodicity.
     * @param framework a { @link Framework } object used to find the start Date in case of monthly periodicity.
     * @return a String represent the start date in the format "yyyy-mm".
     */
    private String findStartDate(Enum.Periodicity periodicity, Optional<Framework> framework, List<String> eventDateList) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
        String startDate = LocalDate.now().format(formatter);

        if(periodicity== Enum.Periodicity.Annually || periodicity== Enum.Periodicity.Bi_Annually || periodicity== Enum.Periodicity.Quarterly) {
            startDate = eventDateList.get(0);
        }
        else if(periodicity== Enum.Periodicity.Monthly) {
            startDate=framework.get().getStartDate();
        }
        return startDate;
    }


    /**
     * Determines the frequency of security compliance based on the new and existing periodicity.
     *
     * @param securityComplianceEditDTO a SecurityComplianceEditDTO containing periodicity information.
     * @param complianceList list of existing security compliance.
     * @param isNewFrequency A boolean flag indicates whether the method is for newFrequency or existingFrequency.
     * @return An Integer representing the frequency of security compliance.
     */
    private Integer findFrequency(SecurityComplianceEditDTO securityComplianceEditDTO, List<SecurityCompliance> complianceList, Boolean isNewFrequency) {
        String newPeriodicity = securityComplianceEditDTO.getPeriodicity().toString();
        String existingPeriodicity = complianceList.get(0).getPeriodicity().toString();
        Integer existingFrequency, newFrequency;

        if(isNewFrequency) {
            //By default, OnEvent frequency is 1 but, it's not allowed to change so make it 13
            if("OnEvent".equals(existingPeriodicity) && (!("OnEvent".equals(newPeriodicity)))) {
                newFrequency = 0;
            } else { //Set frequency based on periodicity type
                newFrequency = periodicityManager.getFrequency(securityComplianceEditDTO.getPeriodicity());
            }
            return newFrequency;
        }
        else {
            //By default, OnEvent frequency is 1 but, it's not allowed to change so make it 0
            if ("OnEvent".equals(newPeriodicity) && (!("OnEvent".equals(existingPeriodicity)))) {
                existingFrequency = 13;
            } else {
                existingFrequency = periodicityManager.getFrequency(complianceList.get(0).getPeriodicity());
            }
            return existingFrequency;
        }
    }


    /**
     * Updates existing security compliance based on the provided {@link SecurityComplianceEditDTO}.
     *
     * @param securityComplianceEditDTO The SecurityComplianceEditDTO containing security compliance details need to be updated.
     * @param complianceList A list of existing security compliance that need to be updated.
     * @param checklist The Checklist object that is associated with the compliance.
     * @param departments The Department object that is associated with the compliance.
     * @param existingCompliance The Compliance associated with the given existing securityId.
     * @param recordId A RecordId used to uniquely identify the compliance.
     * @param creationTime current timestamp, used to set the creation time of the compliance.
     * @param newFrequency The new frequency of the compliance.
     * @param existingFrequency The existing frequency of the compliance.
     * @param startDate The start Date used to generate recordId.
     * @param updatedComplianceList The list Containing all updated compliance DTOs.
     * @param frequencyIndex used to increment the recordId.
     * @param eventListIndex index to traverse the eventList.
     */
    private void updateExistingComplianceRecords(SecurityComplianceEditDTO securityComplianceEditDTO, List<SecurityCompliance> complianceList, Checklist checklist, Set<Department> departments, SecurityCompliance existingCompliance,
                                                 String recordId, LocalDateTime creationTime, Integer newFrequency, Integer existingFrequency, String startDate, List<SecurityComplianceDTO> updatedComplianceList, Integer frequencyIndex, Integer eventListIndex) {

        for (SecurityCompliance compliance : complianceList) {
            // Set creation time, needed for sorting
            creationTime = creationTime.minusSeconds(1);
            compliance.setCreationTime(creationTime);
            compliance.setPeriodicity(securityComplianceEditDTO.getPeriodicity());
            compliance.setChecklist(checklist);
            compliance.setDepartments(departments);
            compliance.setRecordId(recordId);
            compliance.setEvidenceComplianceStatus(securityComplianceEditDTO.getEvidenceComplianceStatus());

            // If evidences are uploaded to the corresponding compliance, then map all of them
            Optional<Evidence> evidenceOpt = evidenceRepository.findById(securityComplianceEditDTO.getSecurityId());
            if (evidenceOpt.isPresent()) {
                Evidence evidence = evidenceOpt.get();
                evidence.setSecurityCompliance(compliance);
            }

            // Save updated compliance to the database
            securityComplianceRepository.save(compliance);

            // Add the compliance to the list
            updatedComplianceList.add(securityComplianceMapper.toSecurityComplianceDTO(compliance));

            //Increment the record ID based on frequency and event date
            recordId = findRecordId(securityComplianceEditDTO, existingFrequency, recordId, frequencyIndex, eventListIndex, startDate);
            frequencyIndex++;  eventListIndex++;
        }
        // Create additional compliance records for remaining periods
        createAdditionalComplianceRecords( securityComplianceEditDTO,  checklist, departments,  existingCompliance, recordId,  creationTime,  newFrequency,
                existingFrequency,  startDate, updatedComplianceList,  frequencyIndex,  eventListIndex);
    }

    /**
     * Creates additional security compliance for the remaining periods based on the new frequency.
     *
     * @param securityComplianceEditDTO The SecurityComplianceEditDTO containing security compliance details need to be updated.
     * @param checklist The Checklist object that is associated with the compliance.
     * @param departments The Department object that is associated with the compliance.
     * @param existingCompliance The Compliance associated with the given existing securityId.
     * @param recordId A RecordId used to uniquely identify the compliance.
     * @param creationTime current timestamp, used to set the creation time of the compliance.
     * @param newFrequency The new frequency of the compliance.
     * @param existingFrequency The existing frequency of the compliance.
     * @param startDate The start Date used to generate recordId.
     * @param updatedComplianceList The list Containing all updated compliance DTOs.
     * @param frequencyIndex used to increment the recordId.
     * @param eventListIndex index to traverse the eventList.
     */
    private void createAdditionalComplianceRecords(SecurityComplianceEditDTO securityComplianceEditDTO, Checklist checklist, Set<Department> departments, SecurityCompliance existingCompliance, String recordId, LocalDateTime creationTime,
                                                   Integer newFrequency, Integer existingFrequency, String startDate, List<SecurityComplianceDTO> updatedComplianceList, Integer frequencyIndex, Integer eventListIndex) {

        for (int i = existingFrequency; i < newFrequency; i++) {

            SecurityCompliance newCompliance = new SecurityCompliance();
            // Set the attributes for new compliance
            creationTime = creationTime.minusSeconds(1);
            newCompliance.setCreationTime(creationTime);
            newCompliance.setComplianceId(existingCompliance.getComplianceId());
            newCompliance.setPeriodicity(securityComplianceEditDTO.getPeriodicity());
            newCompliance.setChecklist(checklist);
            newCompliance.setDepartments(departments);
            newCompliance.setFramework(existingCompliance.getFramework());
            newCompliance.setControl(existingCompliance.getControl());
            newCompliance.setEvidenceComplianceStatus(true);
            newCompliance.setRecordId(recordId);

            // Save the compliance to the database
            SecurityCompliance savedNewCompliance = securityComplianceRepository.save(newCompliance);

            // Add the saved compliance to the list
            updatedComplianceList.add(securityComplianceMapper.toSecurityComplianceDTO(savedNewCompliance));

            //Increment the record ID based on frequency and event date
            recordId = findRecordId(securityComplianceEditDTO, newFrequency, recordId, frequencyIndex, eventListIndex, startDate);
            frequencyIndex++; eventListIndex++;
        }
    }


    /**
     * Generates a new record ID based on the provided parameters.
     *
     * @param securityComplianceEditDTO  The DTO containing information for the periodicity and frequency.
     * @param frequency the existing or new frequency of the compliance.
     * @param recordId A RecordId used to uniquely identify the compliance.
     * @param frequencyIndex used to increment the recordId.
     * @param eventListIndex index to traverse the eventList.
     * @param startDate The start Date used to calculate the recordId.
     * @return A String returning the updated recordId.
     */
    private String findRecordId(SecurityComplianceEditDTO securityComplianceEditDTO, Integer frequency, String recordId, Integer frequencyIndex, Integer eventListIndex, String startDate) {
        if(securityComplianceEditDTO.getPeriodicity().equals(Enum.Periodicity.Monthly)) {
            recordId = RecordIdGenerator.incrementRecordIdMonthly(recordId, frequencyIndex);
        }
        else {
            if(eventListIndex < frequency) {
                startDate = securityComplianceEditDTO.getEventDate().get(eventListIndex);
            }
            recordId = RecordIdGenerator.incrementRecordId(recordId, startDate, frequencyIndex);
        }
        return recordId;
    }


    /**
     * Retrieves a paginated list of filtered security compliance records.
     *
     * @param filter SecurityComplianceFilterDTO containing the filtering criteria to be encapsulated.
     * @return a PaginatedResponse containing SecurityComplianceDTO objects.
     */
    public PaginatedResponse<SecurityComplianceDTO> getFilterSecurityCompliancePaginated(SecurityComplianceFilterDTO filter) {

        //Specification for filtering security compliance based on provided criteria
        Specification<SecurityCompliance> spec = complianceFilter(filter.getFrameworkCategory(), filter.getFramework(), filter.getControl(), filter.getControlCategory(), filter.getComplianceChecklist(), filter.getDepartment(), filter.getEvidenceStatus(), filter.getStatus(), filter.getStartDate(), filter.getEndDate());

        //Pageable object for pagination and sorting
        Pageable pageable= Pagination.createPageableWithMultipleSort(filter.getPage(), filter.getSize());

        //Retrieve paginated list of security compliance
        Page<SecurityCompliance> securityComplianceList = securityComplianceRepository.findAll(spec, pageable);

        //Sort the compliance list if the sort field is not creationTime
        if(!filter.getSortField().equals("creationTime"))
            securityComplianceList = sortComplianceList(securityComplianceList, filter.getSortField(), filter.getSortOrder(), pageable);

        //Map the security compliance entities to DTOs
        Page<SecurityComplianceDTO> securityComplianceDTOPage=securityComplianceList.map(securityComplianceMapper::toSecurityComplianceDTO);

        //Retrieve and set evidences for each DTO
        for(SecurityComplianceDTO securityComplianceDTO: securityComplianceDTOPage) {
            List<String> evidences = evidenceRepository.findFileNameBySecurityId(securityComplianceDTO.getSecurityId());
            securityComplianceDTO.setEvidenceList(evidences);
        }
        return Pagination.createdPaginatedContent(securityComplianceDTOPage);
    }


    /**
     * Create a Specification for filtering security compliance based on various criteria.
     *
     * @param frameworkCategory the list of frameworkCategory to filter by.
     * @param frameworks the list of frameworks to filter by.
     * @param controls the list of controls to filter by.
     * @param controlCategories the list of controlCategory to filter by.
     * @param complianceChecklists the list of checklist to filter by.
     * @param departments the list of departments to filter by.
     * @param evidenceStatus the evidenceStatus to filter by.
     * @param status the security compliance status to filter by.
     * @param startDate the framework startDate to filter by.
     * @param endDate the framework endDate to filter by.
     * @return the specification for filtering security compliance entity.
     */
    private Specification<SecurityCompliance> complianceFilter(List<String> frameworkCategory, List<String> frameworks, List<String> controls, List<String> controlCategories, List<String> complianceChecklists, List<String> departments, String evidenceStatus, Boolean status, String startDate, String endDate) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            List<String> departmentList = null;

            //Check if there is an Authenticated User
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("No authenticated user found");
            }
            //If authenticated, check for LDAP user details
            else if (authentication.getPrincipal() instanceof LdapUserDetailsImpl ldapUserDetails) {
                log.info("Authenticated LDAP user: {}", ldapUserDetails.getUsername());

                //Retrieve List of all Departments associated to the logged-in user
                departmentList = usersRepository.findAllDepartmentByUserEmailId(ldapUserDetails.getUsername());
                log.info("users departments {}", departmentList);
            } else {
                log.error("Principal is not of type LdapUserDetailsImpl, but of type: {}", authentication.getPrincipal().getClass().getName());
            }

            //Framework Category Filter
            if (frameworkCategory != null && !frameworkCategory.isEmpty()) {
                predicates.add(root.get("framework").get("frameworkCategory").get("frameworkCategoryName").in(frameworkCategory));
            }

            //Frameworks Filter
            if (frameworks != null && !frameworks.isEmpty()) {
                log.info("in framework filter now");
                predicates.add(root.get("framework").get("frameworkName").in(frameworks));
            }

            //Controls Filter
            if (controls != null && !controls.isEmpty()) {
                predicates.add(root.get("control").get("controlName").in(controls));
            }

            //Control Category Filter
            if (controlCategories != null && !controlCategories.isEmpty()) {
                predicates.add(root.get("control").get("controlCategory").get("controlCategoryName").in(controlCategories));
            }

            //Checklist Filter
            if (complianceChecklists != null && !complianceChecklists.isEmpty()) {
                predicates.add(root.get("checklist").get("controlChecklist").in(complianceChecklists));
            }

            // Department Filter
//            if(departmentList != null && !departmentList.isEmpty()) {
//
//                //Check if the user has access to the specified departments
//                if(departments != null && !departments.isEmpty() && (departmentList.containsAll(departments) || departmentList.contains(String.valueOf(Enum.DepartmentType.Administration)))) {
//                    Join<SecurityCompliance, Department> departmentJoin = root.join("departments", JoinType.INNER);
//
//                    Predicate departmentPredicate = departmentJoin.get("departmentName").in(departments);
//
//                    //Subquery to ensure the count of distinct departments matches the input departments
//                    Subquery<Long> subquery = query.subquery(Long.class);
//                    Root<SecurityCompliance> subqueryRoot = subquery.from(SecurityCompliance.class);
//                    Join<SecurityCompliance, Department> subqueryDepartmentJoin = subqueryRoot.join("departments", JoinType.INNER);
//
//                    subquery.select(criteriaBuilder.countDistinct(subqueryDepartmentJoin.get("departmentName")))
//                            .where(criteriaBuilder.equal(subqueryRoot, root));
//
//
//                    Predicate countPredicate = criteriaBuilder.equal(subquery, (long) departments.size());
//
//                    // Subquery to remove extra department
//                    Subquery<Long> extraDepartmentsSubquery = query.subquery(Long.class);
//                    Root<SecurityCompliance> extraDepartmentsRoot = extraDepartmentsSubquery.from(SecurityCompliance.class);
//                    Join<SecurityCompliance, Department> extraDepartmentsJoin = extraDepartmentsRoot.join("departments", JoinType.INNER);
//
//                    extraDepartmentsSubquery.select(criteriaBuilder.count(extraDepartmentsJoin.get("departmentName")))
//                            .where(criteriaBuilder.and(
//                                    criteriaBuilder.equal(extraDepartmentsRoot, root),
//                                    extraDepartmentsJoin.get("departmentName").in(departments).not()
//                            ));
//
//                    Predicate extraDepartmentsPredicate = criteriaBuilder.equal(extraDepartmentsSubquery, 0L);
//                    //Combine department predicates
//                    predicates.add(criteriaBuilder.and(departmentPredicate, countPredicate, extraDepartmentsPredicate));
//                }
//                else {
//                    ///If not an admin, filter by user-specific departments
//                    if(!departmentList.contains(String.valueOf(Enum.DepartmentType.Administration))) {
//                        log.info("else departments is null {}",departmentList);
//                        Predicate departmentPredicate = root.join("departments").get("departmentName").in(departmentList);
//                        predicates.add(departmentPredicate);
//                    }
//                }
//            }

            if(departmentList != null && !departmentList.isEmpty()) {

                // Check if the user has access to the specified departments
                if(departments != null && !departments.isEmpty() && (departmentList.containsAll(departments) || departmentList.contains(String.valueOf(Enum.DepartmentType.Administration)))) {
                    Join<SecurityCompliance, Department> departmentJoin = root.join("departments", JoinType.INNER);

                    Predicate departmentPredicate = departmentJoin.get("departmentName").in(departments);

                    // Combine department predicates using OR condition
                    predicates.add(departmentPredicate);
                }
                else {
                    // If not an admin, filter by user-specific departments
                    if(!departmentList.contains(String.valueOf(Enum.DepartmentType.Administration))) {
                        log.info("else departments is null {}", departmentList);
                        Predicate departmentPredicate = root.join("departments").get("departmentName").in(departmentList);
                        predicates.add(departmentPredicate);
                    }
                }
            }

            //Evidence Status Filter
            if (evidenceStatus != null) {
                predicates.add(criteriaBuilder.equal(root.get("evidenceComments"), evidenceStatus));
            }

            //Security Compliance Status Filter
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("evidenceComplianceStatus"), status));
            }

            //Framework Start Date Filter
            if (startDate != null && !startDate.isEmpty()) {
                String startPattern = convertToYearMonthPattern(startDate);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        criteriaBuilder.function("SUBSTRING", String.class, root.get("recordId"),
                                criteriaBuilder.literal(7),
                                criteriaBuilder.literal(5)),
                        startPattern));
            }

            //Framework End Date Filter
            if (endDate != null && !endDate.isEmpty()) {
                String endPattern = convertToYearMonthPattern(endDate);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        criteriaBuilder.function("SUBSTRING", String.class, root.get("recordId"),
                                criteriaBuilder.literal(7),
                                criteriaBuilder.literal(5)),
                        endPattern));
            }
            //Combine all predicates into a single Predicate for the query
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


    /**
     * Convert a date string to year-month pattern.
     *
     * @param date the input date in "yyyy-MM" format
     * @return the formatted date in year-month pattern.
     */
    private String convertToYearMonthPattern(String date) {
        //Parse the input date string and append " 01" to represent the first day of the month
        LocalDate localDate = LocalDate.parse(date + " 01", INPUT_FORMATTER);

        return localDate.format(OUTPUT_FORMATTER);
    }


    /**
     * Sorts a page of SecurityCompliance based on the specified sortField and sortOrder.
     *
     * @param securityComplianceList the security compliance page which needs to sort.
     * @param sortField the field to sort by.
     * @param sortOrder the order to sort, either ascending or descending.
     * @param pageable the pageable information.
     * @return a sorted security compliance page.
     */
    private Page<SecurityCompliance> sortComplianceList(Page<SecurityCompliance> securityComplianceList, String sortField, String sortOrder, Pageable pageable) {
        //Get the content of current page
        List<SecurityCompliance> complianceList = securityComplianceList.getContent();

        // Comparator for sorting the security compliance
        Comparator<SecurityCompliance> comparator = (sc1, sc2) -> {
            //Determine logic to compare fields based on sortField
            switch (sortField) {
                case "recordId": // Sort by recordId
                    return compareValues(sc1.getRecordId(), sc2.getRecordId());

                case "frameworkName": // Sort by framework name
                    return compareValues(sc1.getFramework().getFrameworkName(), sc2.getFramework().getFrameworkName());

                case "frameworkCategory": // Sort by frameworkCategory name
                    return compareValues(sc1.getFramework().getFrameworkCategory().getFrameworkCategoryName(), sc2.getFramework().getFrameworkCategory().getFrameworkCategoryName());

                case "controlName": // Sort by control name
                    return compareValues(sc1.getControl().getControlName(), sc2.getControl().getControlName());

                case "controlCategory": // Sort by controlCategory name
                    return compareValues(sc1.getControl().getControlCategory().getControlCategoryName(), sc2.getControl().getControlCategory().getControlCategoryName());

                case "periodicity": // Sort by periodicity
                    return compareValues(sc1.getPeriodicity(), sc2.getPeriodicity());

                default:
                    throw new IllegalArgumentException("Invalid sort field: " + sortField);
            }
        };

        // Reverse the comparator if sortOrder is descending
        if ("asc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }

        // Sort and collect the list
        List<SecurityCompliance> sortedList = complianceList.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        //Return a PageImpl containing the sorted list of security compliance
        return new PageImpl<>(sortedList, pageable, securityComplianceList.getTotalElements());
    }


    /**
     * Compares two Comparable values and handling nulls.
     *
     * @param value1 the value to compare.
     * @param value2 the value to compare.
     * @return a positive integer or a negative integer or zeo based on comparison.
     */
    private int compareValues(Comparable value1, Comparable value2) {
        //If both value are null, then consider them equal
        if (value1 == null && value2 == null) return 0;

        //If value1 is null, consider value2 as greater
        if (value1 == null) return -1;

        //If value2 is null, consider value1 as greater
        if (value2 == null) return 1;

        //Compare two values
        return value1.compareTo(value2);
    }


    /**
     * Exports security compliance to an Excel.
     *
     * @param filter SecurityComplianceFilterDTO containing the filtering criteria to be encapsulated.
     * @return a ByteArrayResource containing the Excel file data.
     * @throws IOException if an error occurs during export.
     */
    public ByteArrayResource exportExcelSecurityCompliance(SecurityComplianceFilterDTO filter) throws IOException {
        log.info("Starting export of security compliance data to Excel");

        // Retrieve paginated list of filtered security compliance DTO.
        PaginatedResponse<SecurityComplianceDTO> paginatedResponse = getFilterSecurityCompliancePaginated(filter);
        List<SecurityComplianceDTO> securityComplianceList = paginatedResponse.getContent(); // Get the content from the paginated response
        log.debug("Fetched {} security compliance records for export", securityComplianceList.size());


        // Output stream and configuration for Excel export
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        List<String> fieldsToInclude = List.of(
                "recordId",
                "frameworkName",
                "frameworkCategory",
                "controlName",
                "controlCategory",
                "checklistName",
                "periodicity",
                "departments",
                "evidenceList",
                "evidenceComplianceStatus",
                "evidenceComments"
        );

        //Custom headers for Excel Columns.
        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("recordId", "RECORD ID");
        customHeaders.put("frameworkName", "FRAMEWORK NAME");
        customHeaders.put("frameworkCategory", "FRAMEWORK CATEGORY");
        customHeaders.put("controlName", "CONTROL NAME");
        customHeaders.put("controlCategory", "CONTROL CATEGORY");
        customHeaders.put("checklistName", "CHECKLIST NAME");
        customHeaders.put("periodicity", "PERIODICITY");
        customHeaders.put("departments", "DEPARTMENTS");
        customHeaders.put("evidenceList", "EVIDENCE LIST");
        customHeaders.put("evidenceComplianceStatus", "STATUS");
        customHeaders.put("evidenceComments", "EVIDENCE STATUS");

        //Call exportExcel to export data to excel.
        exportExcel.exportToExcel(outputStream, securityComplianceList, "security-compliance", fieldsToInclude, customHeaders);
        log.info("Successfully exported security compliance data to Excel");

        //Convert Output Stream to ByteArrayResource
        byte[] excelContent = outputStream.toByteArray();
        ByteArrayResource resource = new ByteArrayResource(excelContent);
        return resource;
    }


    /**
     * Retrieves a list of all security compliance from the database.
     *
     * @return a list of {@link SecurityComplianceDTO} containing all security compliance along with their evidences.
     */
    public List<SecurityComplianceDTO> getAllSecurityComplianceDTOs() {
        log.info("Fetching all security compliance records with evidence");

        //Retrieve all compliance records from the repository
        List<SecurityCompliance> complianceList = securityComplianceRepository.findAll();
        log.debug("Retrieved {} security compliance records from the repository", complianceList.size());


        //Map compliance records to DTOs along with evidence list
        return complianceList.stream()
                .map(this::mapToDTOWithEvidence)
                .collect(Collectors.toList());
    }


    /**
     * Map Security compliance entity to a security compliance DTO along with associated evidences.
     *
     * @param compliance the SecurityCompliance entity to map.
     * @return a {@link SecurityComplianceDTO} containing the compliance details and evidence list.
     */
    public SecurityComplianceDTO mapToDTOWithEvidence(SecurityCompliance compliance) {
        log.debug("Mapping security compliance with ID {} to DTO", compliance.getSecurityId());

        //Fetch evidence list by securityId
        List<String> evidenceNames = evidenceRepository.findFileNameBySecurityId(compliance.getSecurityId());
        log.debug("Fetched {} evidence files for security compliance ID {}", evidenceNames.size(), compliance.getSecurityId());

        // Map the SecurityCompliance entity to DTO, including the evidence list and return
        return securityComplianceMapper.toSecurityComplianceDTO(compliance, evidenceNames);
    }


    /**
     * Retrieves a list of SecurityComplianceDTO for the given list of complianceId.
     *
     * @param complianceIds a list of complianceId whose associated security compliance need to retrieve.
     * @return a list of lists containing {@link SecurityComplianceDTO} corresponding to each complianceId.
     * @throws SecurityComplianceNotFoundException if a security ID is not found in the repository.
     */
    @Override
    public List<List<SecurityComplianceDTO>> getSecurityCompliance(List<String> complianceIds) throws SecurityComplianceNotFoundException {
        //List to add securityComplianceDTO
        List<List<SecurityComplianceDTO>> securityComplianceList = new ArrayList<>();

        //Iterate through each complianceId in the list
        for(String complianceId : complianceIds){
            List<SecurityCompliance> securityCompliance = securityComplianceRepository.findAllByComplianceId(complianceId);

            //Check if the retrieved list is empty, then throw an exception
            if (securityCompliance.isEmpty()) {
                throw new SecurityComplianceNotFoundException();
            }

            //Map the entity to the DTO and add it to the list
            List<SecurityComplianceDTO> complianceDTOs = securityComplianceMapper.toSecurityComplianceDTO(securityCompliance);
            securityComplianceList.add(complianceDTOs);
        }
        return securityComplianceList;
    }


    /**
     * To check whether a security compliance already exists in the database.
     *
     * @param securityComplianceCheckDTO SecurityComplianceCheckDTO containing records to check against existing records.
     * @return {@code true} if the security compliance already exists, {@code false} otherwise.
     */
    @Override
    public Boolean isSecurityComplianceAlreadyExists(SecurityComplianceCheckDTO securityComplianceCheckDTO) {
        log.debug("Attempting to get compliance status {}", securityComplianceCheckDTO);

        // Check whether we're in update mode (checklist is null) or add mode
        boolean isEditMode = securityComplianceCheckDTO.getChecklist() == null;

        if (isEditMode) {
            // In edit mode, we only check based on framework, control, and periodicity
            List<SecurityCompliance> securityComplianceList = securityComplianceRepository
                    .findByFramework_FrameworkNameAndControl_ControlNameAndPeriodicityAndEvidenceComplianceStatus(
                            securityComplianceCheckDTO.getFramework(),
                            securityComplianceCheckDTO.getControl(),
                            securityComplianceCheckDTO.getPeriodicity(),
                            true);

            return !securityComplianceList.isEmpty();
        } else {
            // In add mode, we check including the checklist
            Set<Checklist> checklists = checklistRepository.findByControlChecklistInAndControl_ControlName(securityComplianceCheckDTO.getChecklist(), securityComplianceCheckDTO.getControl());
            for (Checklist checklist : checklists) {
                List<SecurityCompliance> securityComplianceList = securityComplianceRepository
                        .findByFramework_FrameworkNameAndControl_ControlNameAndChecklist_ControlChecklistAndPeriodicityAndEvidenceComplianceStatus(
                                securityComplianceCheckDTO.getFramework(),
                                securityComplianceCheckDTO.getControl(),
                                checklist.getControlChecklist(),
                                securityComplianceCheckDTO.getPeriodicity(),
                                true);

                if (!securityComplianceList.isEmpty()) {
                    return true;
                }
            }
            return false;
        }
    }
}

