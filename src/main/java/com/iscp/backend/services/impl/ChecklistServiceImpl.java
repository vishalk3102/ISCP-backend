package com.iscp.backend.services.impl;


import com.iscp.backend.components.ExportExcel;
import com.iscp.backend.components.Pagination;
import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.ChecklistAlreadyExistsException;
import com.iscp.backend.exceptions.ChecklistNotFoundException;
import com.iscp.backend.exceptions.ControlNotFoundException;
import com.iscp.backend.mapper.ChecklistMapper;
import com.iscp.backend.models.Checklist;
import com.iscp.backend.models.Control;
import com.iscp.backend.repositories.ChecklistRepository;
import com.iscp.backend.repositories.ControlRepository;
import com.iscp.backend.services.ChecklistService;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ChecklistServiceImpl implements ChecklistService {


    private final ChecklistRepository checklistRepository;

    private final ChecklistMapper checklistMapper;

    private final ControlRepository controlRepository;

    private final ExportExcel exportExcel;


    //FUNCTION TO GET  CHECKLIST DETAILS
    @Override
    public ChecklistDTO getChecklistByName(String checklistName, String controlName) throws ChecklistNotFoundException {

        log.debug("Attempting to get checklist details");

        Optional<Checklist> checklist = checklistRepository.findByControlChecklistAndControl_ControlName(checklistName,controlName);
        if(checklist.isEmpty())
        {
            log.error("Checklist Name not Found {}",checklistName);
            throw new ChecklistNotFoundException();
        }
        return checklistMapper.toChecklistDTO(checklist.get());
    }

    //FUNCTION TO GET ALL CHECKLIST
    @Override
    public List<ChecklistDTO> getAllChecklists() {
        log.debug("Attempting to get all  Checklist");

        // Retrieve all checklist from the repository
        List<Checklist> checklists=checklistRepository.findAll(Sort.by(Sort.Direction.ASC,"controlChecklist"));

        // Check if the retrieved list is empty
        if (checklists.isEmpty())
        {
            log.info("No checklists found");
            return List.of();
        }
        log.info("Fetched checklists :{}",checklists);

        // Remove duplicates by name using Stream API
        List<Checklist> uniqueChecklists = checklists.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Checklist::getControlChecklist))),
                        ArrayList::new
                ));

        return  checklistMapper.toChecklistDTOs(uniqueChecklists);
    }

    //  FUNCTION TO ADD/UPDATE CHECKLIST
    @Override
    public List<ChecklistDTO> addEditCheckList(List<ChecklistCreateDTO> checklistCreateDTOList) throws ChecklistNotFoundException, ChecklistAlreadyExistsException, ControlNotFoundException {

        List<ChecklistUpdateDTO> checklistUpdateDTOS = new ArrayList<>();
        List<ChecklistDTO> checklistDTOList = new ArrayList<>();


        for(ChecklistCreateDTO checklistDTO :checklistCreateDTOList)
        {
            String id = checklistDTO.getChecklistId();
            if(id == null || id.isBlank())
            {
                log.debug("Attempting to add/edit {} checklists", checklistCreateDTOList.size());
                checklistDTOList.add(addChecklist(checklistDTO));
            }
            else if(checklistRepository.findById(id).isEmpty())
            {
                log.debug("Adding new checklist with empty ID");
                throw new ChecklistNotFoundException();
            }
            else
            {
                log.error("Checklist not found with  id {}", id);
                checklistUpdateDTOS.add(checklistMapper.toChecklistUpdateDTO(checklistDTO));
            }

        }

        List<ChecklistDTO> updatedChecklists = updateChecklists(checklistUpdateDTOS);
        checklistDTOList.addAll(updatedChecklists);

        log.info("Successfully processed {} controls", checklistDTOList.size());
        return  checklistDTOList;
    }

    //FUNCTION TO ADD CHECKLIST
    @Override
    public ChecklistDTO addChecklist(ChecklistCreateDTO checklistCreateDTO) throws ChecklistAlreadyExistsException, ControlNotFoundException {

        log.debug("Adding new checklist: {}", checklistCreateDTO);

        // Check if a checklist with the same name already exists
        Optional<Checklist> existingChecklist=checklistRepository.findByControlChecklistAndControl_ControlName(checklistCreateDTO.getControlChecklist(),checklistCreateDTO.getControlName());
        if(existingChecklist.isPresent())
        {
            log.warn("Checklist with name {} already exists for control {}",
                    checklistCreateDTO.getControlChecklist(),
                    checklistCreateDTO.getControlName());
            throw  new ChecklistAlreadyExistsException();
        }

        Optional<Control> control = controlRepository.findByControlName(checklistCreateDTO.getControlName());
        if(control.isEmpty())
        {
            log.error("Control not Found");
            throw new ControlNotFoundException();
        }
        // Create and save the new Checklist
        Checklist checklist=checklistMapper.toChecklistEntity(checklistCreateDTO);
        checklist.setControl(control.get());
        checklist.setCreationTime(LocalDateTime.now());
        Checklist savedChecklist=checklistRepository.save(checklist);

        log.info("Checklist added successfully with id: {}", savedChecklist.getChecklistId());
        return checklistMapper.toChecklistDTO(savedChecklist);
    }

    //FUNCTION TO UPDATE CHECKLIST
    @Override
    public List<ChecklistDTO> updateChecklists(List<ChecklistUpdateDTO> checklistUpdateDTOs) {
        log.debug("Attempting to update Checklists");

        List<Checklist> updatedChecklists=checklistUpdateDTOs.stream().map(checklistUpdateDTO -> {
            try {
                Checklist checklist = updateSingleChecklist(checklistUpdateDTO);
                checklist.setCreationTime(LocalDateTime.now());
                return checklist;
            }
            catch (ChecklistNotFoundException e)
            {
                log.error("Checklist not found with id {}",checklistUpdateDTO.getChecklistId());
                throw new RuntimeException(e);
            }
            catch (ControlNotFoundException e)
            {
                log.error("Control Name not Found ");
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());


        // Update and save all the checklist
        List<Checklist> savedChecklist=checklistRepository.saveAll(updatedChecklists);
        log.info("Updated {} checklist successfully ", savedChecklist.size());


        return  savedChecklist.stream().map(checklistMapper::toChecklistDTO).collect(Collectors.toList());

    }


    //FUNCTION TO UPDATE SINGLE CHECKLIST
    @Override
    public Checklist updateSingleChecklist(ChecklistUpdateDTO checklistUpdateDTO) throws ChecklistNotFoundException, ControlNotFoundException {
        log.debug("Attempting to update Checklist with id: {}", checklistUpdateDTO.getChecklistId());

        // Find the existing Checklist
        Optional<Checklist> checklist=checklistRepository.findById(checklistUpdateDTO.getChecklistId());
        if(checklist.isEmpty()) {
            throw new ChecklistNotFoundException();
        }

        Optional<Control> control = controlRepository.findByControlName(checklistUpdateDTO.getControlName());
        if(control.isEmpty())
        {
            throw new ControlNotFoundException();
        }

        // Map ChecklistupdateDto to Checklist entity
        checklistMapper.updateChecklistEntityFromDTO(checklistUpdateDTO,checklist.get());
        checklist.get().setControl(control.get());
        return checklist.get();
    }


    //FUNCTION TO GET ALL CHECKLIST BY CONTROL NAME
    @Override
    public List<ChecklistDTO> getChecklistFromControl(String controlName) throws ControlNotFoundException {

        log.debug("Attempting to get all the checklist by control name");

        Optional<Control> control = controlRepository.findByControlName(controlName);
        if(control.isEmpty())
        {
            log.error("Control Name not Found {}",controlName);
            throw new ControlNotFoundException();
        }

        List<Checklist> checklistList = checklistRepository.findByControl_ControlName(controlName,Sort.by(Sort.Direction.ASC, "controlChecklist"));

        return checklistMapper.toChecklistDTOs(checklistList);

    }


    //FUNCTION TO GET ALL CHECKLIST ASSOCIATED TO LIST OF  CONTROL NAME
    @Override
    public List<ChecklistDTO> getChecklistFromControlList(List<String> controlName) throws ControlNotFoundException {
        log.info("Attempting to get all the checklist by control name list {}",controlName);
        for(String control: controlName)
        { Optional<Control> optionalControl = controlRepository.findByControlName(control);
            if(optionalControl.isEmpty())
            {
                log.error("ControlName not Found in getChecklistFromControlList {}",controlName);
                throw new ControlNotFoundException();
            }
        }
        List<Checklist> checklistList = checklistRepository.findByControlList(controlName);
        return checklistMapper.toChecklistDTOs(checklistList);
    }


    //FUNCTION FOR SEARCH FILTER
    @Override
    public PaginatedResponse<ChecklistDTO> getFilterChecklistPaginated(ChecklistFilterDTO filterDTO) {
        //Specification for filtering users based on provided criteria
        Specification<Checklist> spec =  checklistFilter(filterDTO.getControl(),filterDTO.getChecklist(),filterDTO.getStatus());


        String sortField = filterDTO.getSortField(); ;

        if("controlName".equals(sortField))
        {
            sortField  = "control";
        }

        //Pageable object for pagination and sorting
        Pageable pageable = Pagination.createPageable(filterDTO.getPage(), filterDTO.getSize(), sortField,filterDTO.getSortOrder());

        //Retrieve paginated list of users
        Page<Checklist> checklistPage = checklistRepository.findAll(spec,pageable);

        //Map User to UserDTO
        Page<ChecklistDTO> checklistDTOPage  =checklistPage.map(checklistMapper::toChecklistDTO);

        return  Pagination.createdPaginatedContent(checklistDTOPage);
    }


    //FUNCTION TO CREATE SPECIFICATION FOR SEARCH FUNCTIONALITY
    private Specification<Checklist> checklistFilter(List<String> controls,List<String> controlChecklist, Boolean status)
    {
        return (root, query, criteriaBuilder) ->
        {
            List<Predicate> predicates = new ArrayList<>();
            if (controls != null && !controls.isEmpty()) {
                predicates.add(root.get("control").get("controlName").in(controls));
            }
            if(controlChecklist != null && !controlChecklist.isEmpty())
            {
                predicates.add(root.get("controlChecklist").in(controlChecklist));
                log.info("Added predicate for check list: {}",controlChecklist);
            }
            if(status!=null)
            {
                predicates.add(criteriaBuilder.equal(root.get("status"),status));
                log.info("Added predicate for status: {}", status);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


    //  FUNCTION TO EXPORT CHECKLIST
    @Override
    public ByteArrayResource exportExcelChecklist(ChecklistFilterDTO filter) throws IOException {
        log.info("Starting export of checklist data to Excel");

        // Retrieve all checklist DTOs
        PaginatedResponse<ChecklistDTO> paginatedResponse= getFilterChecklistPaginated(filter);
        List<ChecklistDTO> checklistList = paginatedResponse.getContent();
        log.debug("Fetched {} checklist records for export", checklistList.size());

        // Output stream and configuration for Excel export
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        List<String> fieldsToInclude = List.of(
                "controlChecklist",
                "description",
                "status"
        );
        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("controlChecklist", "COMPLIANCE CHECKLIST");
        customHeaders.put("description", "DESCRIPTION");
        customHeaders.put("status", "STATUS");

        // Use the existing export method
        exportExcel.exportToExcel(outputStream, checklistList, "checklist", fieldsToInclude, customHeaders);
        log.info("Successfully exported checklist data to Excel");

        // Convert Output Stream to ByteArrayResource
        byte[] excelContent = outputStream.toByteArray();
        return new ByteArrayResource(excelContent);
    }


    //FUNCTION TO PARSE CSV FILE TO DTO
    public List<ChecklistCreateDTO> parseCSVFile(MultipartFile file) throws IOException {
        List<ChecklistCreateDTO> checklistCreateDTOS = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header row
                }
                String[] values = line.split(",");
                ChecklistCreateDTO dto = new ChecklistCreateDTO();

                // Handle the controlId field
                String checklistId = values[0].trim();
                if (checklistId.isEmpty() || checklistId.equals("\"\"")) {
                    dto.setChecklistId(null);
                } else {
                    dto.setChecklistId(checklistId.replaceAll("^\"|\"$", ""));
                }

                dto.setControlChecklist(removeQuotes(values[1].trim()));
                dto.setDescription(removeQuotes(values[2].trim()));
                dto.setStatus(Boolean.parseBoolean(removeQuotes(values[3].trim())));


                // Handle the controlCategoryName field specifically
                String controlName = removeQuotes(values[4].trim());
                if (controlName.endsWith("\"")) {
                    controlName = controlName.substring(0, controlName.length() - 1);
                }
                dto.setControlName(controlName);
                checklistCreateDTOS.add(dto);
            }
        }

        return checklistCreateDTOS;
    }


    //FUNCTION TO REMOVE QUOTATION MARKS
    private String removeQuotes(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
