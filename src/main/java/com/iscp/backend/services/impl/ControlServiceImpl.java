package com.iscp.backend.services.impl;


import com.iscp.backend.components.ExportExcel;
import com.iscp.backend.components.Pagination;
import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.ControlAlreadyExistsException;
import com.iscp.backend.exceptions.ControlCategoryNotFoundException;
import com.iscp.backend.exceptions.ControlNotFoundException;
import com.iscp.backend.models.Control;
import com.iscp.backend.models.ControlCategory;
import com.iscp.backend.mapper.ControlMapper;
import com.iscp.backend.models.SecurityCompliance;
import com.iscp.backend.repositories.ControlCategoryRepository;
import com.iscp.backend.repositories.ControlRepository;
import com.iscp.backend.services.ControlService;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
public class ControlServiceImpl implements ControlService {

    private final ControlRepository controlRepository;

    private final ControlCategoryRepository controlCategoryRepository;

    private final ControlMapper controlMapper;

    private final ExportExcel exportExcel;

    //FUNCTION TO GET ALL CONTROLS
    @Override
    public List<ControlDTO> getAllControls() {
        log.debug("Attempting to get all  Controls");

        // Retrieve all controls from the repository
        List<Control> controls=controlRepository.findAll(Sort.by(Sort.Direction.ASC,"controlName"));

        // Check if the retrieved list is empty
        if (controls.isEmpty())
        {
            log.info("No controls found");
            return List.of();
        }
        log.info("Fetched controls :{}",controls);
        return  controlMapper.toControlDTOs(controls);
    }

    //FUNCTION TO ADD/UPDATE  CONTROL
    @Override
    public List<ControlDTO> addEditControl(List<ControlCreateDTO> controlCreateDTOList) throws ControlAlreadyExistsException, ControlCategoryNotFoundException, ControlNotFoundException {
        log.debug("Attempting to add/edit {} controls", controlCreateDTOList.size());

        List<ControlUpdateDTO> controlUpdateDTOS = new ArrayList<>();
        List<ControlDTO> controlDTOList = new ArrayList<>();


        for(ControlCreateDTO controlDTO :controlCreateDTOList)
        {
            String id = controlDTO.getControlId();
            if(id == null || id.isBlank())
            {
                log.debug("Adding new control with empty ID");
                controlDTOList.add(addControl(controlDTO));
            }
            else if(controlRepository.findByControlId(id).isEmpty())
            {
                log.error("Control not found with id {}", id);
                throw new ControlNotFoundException();
            }
            else
            {
                log.debug("Preparing to update control with ID: {}", id);
                controlUpdateDTOS.add(controlMapper.toControlUpdateDTO(controlDTO));
            }

        }

        List<ControlDTO> updatedControls = updateControls(controlUpdateDTOS);
        controlDTOList.addAll(updatedControls);

        log.info("Successfully processed {} controls", controlDTOList.size());
        return  controlDTOList;
    }

    //FUNCTION TO ADD CONTROL
    @Override
    public ControlDTO addControl(ControlCreateDTO controlCreateDTO) throws ControlCategoryNotFoundException, ControlAlreadyExistsException {
        log.debug("Attempting to add Control: {}", controlCreateDTO);

        // Find the associated control category
        Optional<ControlCategory> controlCategory=controlCategoryRepository.findByControlCategoryName(controlCreateDTO.getControlCategoryName());
        if(controlCategory.isEmpty())
        {
            log.warn("Control Category with name {}  not found",controlCreateDTO.getControlCategoryName());
            throw  new ControlCategoryNotFoundException();
        }

        // Check if a control with the same name already exists
        Optional<Control> existingControl=controlRepository.findByControlName(controlCreateDTO.getControlName());
        if(existingControl.isPresent())
        {
            log.warn("Control  with name {}  already exists",controlCreateDTO.getControlName());
            throw  new ControlAlreadyExistsException();
        }

        // Create and save the new Control
        Control control= controlMapper.toControlEntity(controlCreateDTO);
        control.setControlCategory(controlCategory.get());
        control.setCreationTime(LocalDateTime.now());
        Control savedControl= controlRepository.save(control);

        log.info("Control added successfully with id: {}", savedControl.getControlId());
        return controlMapper.toControlDTO(savedControl);
    }

    //FUNCTION TO UPDATE CONTROL
    @Override
    public List<ControlDTO> updateControls(List<ControlUpdateDTO> controlUpdateDTOs) throws ControlNotFoundException {
        log.debug("Attempting to update Controls");

        List<Control> updatedControls=controlUpdateDTOs.stream().map(controlUpdateDTO -> {
            try {
                Control control =  updateSingleControl(controlUpdateDTO);
                control.setCreationTime(LocalDateTime.now());
                return control;
            }
            catch (ControlNotFoundException e)
            {
                log.error("Control not found with id {}",controlUpdateDTO.getControlId());
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());


        // Update and save all the controls
        List<Control> savedControls=controlRepository.saveAll(updatedControls);
        log.info("Updated {} controls successfully ", savedControls.size());


        return  savedControls.stream().map(controlMapper::toControlDTO).collect(Collectors.toList());
    }

    //FUNCTION TO UPDATE SINGLE CONTROL
    @Override
    public Control updateSingleControl(ControlUpdateDTO controlUpdateDTO) throws ControlNotFoundException {
        log.debug("Attempting to update Control with id: {}", controlUpdateDTO.getControlId());

        // Find the existing Control
        Optional<Control> control=controlRepository.findById(controlUpdateDTO.getControlId());
        if(control.isEmpty()) {
            throw new ControlNotFoundException();
        }

        // Map controlUpdateDTO to control entity
        controlMapper.updateControlEntityFromDTO(controlUpdateDTO,control.get());
        return control.get();
    }


    //FUNCTION TO GET CONTROL ASSOCIATED TO A CONTROL CATEGORY
    @Override
    public List<ControlDTO> getControlFromControlCategory(String controlCategoryName) throws ControlCategoryNotFoundException {

        log.debug("Attempting to get all control");

        Optional<ControlCategory> controlCategory = controlCategoryRepository.findByControlCategoryName(controlCategoryName);

        if(controlCategory.isEmpty())
        {
            log.error("Control Category Not Found");
            throw new ControlCategoryNotFoundException();
        }

        List<Control> controlList = controlRepository.findByControlCategory_ControlCategoryName(controlCategoryName,Sort.by(Sort.Direction.ASC, "controlName"));
        log.info("Fetched controls using control category name:{}",controlList);

        if(controlList.isEmpty())
        {
            return List.of();
        }
        return controlMapper.toControlDTOs(controlList);
    }

    //FUNCTION FOR SEARCH FILTER
    @Override
    public PaginatedResponse<ControlDTO> getFilterControlPaginated(ControlFilterDTO filterDTO) {

        Specification<Control> spec = controlFilter(filterDTO.getControlCategory(),filterDTO.getControl(),filterDTO.getStatus());
        Pageable pageable = Pagination.createPageable(filterDTO.getPage(), filterDTO.getSize(), filterDTO.getSortField(), filterDTO.getSortOrder());
        Page<Control> controlPage = controlRepository.findAll(spec,pageable);
        Page<ControlDTO> controlDTOPage =controlPage.map(controlMapper::toControlDTO);

        return  Pagination.createdPaginatedContent(controlDTOPage);
    }

//    private Page<Control> sortList(Page<Control> controls, String sortField, Boolean sortOrder, Pageable pageable) {
//        //Get the content of current page
//        List<Control> controlList = controls.getContent();
//
//        // Comparator for sorting the security compliance
//        Comparator<Control> comparator = (sc1, sc2) -> {
//            //Determine logic to compare fields based on sortField
//            switch (sortField) {
//                case "controlName": // Sort by recordId
//                    return compareValues(sc1.getControlName(), sc2.getControlName());
//
//                case "controlCategory": // Sort by framework name
//                    return compareValues(sc1.getControlCategory().getControlCategoryName(), sc2.getControlCategory().getControlCategoryName());
//
//                default:
//                    throw new IllegalArgumentException("Invalid sort field: " + sortField);
//            }
//        };
//
//        // Reverse the comparator if sortOrder is descending
//        if (sortOrder) {
//            comparator = comparator.reversed();
//        }
//
//        // Sort and collect the list
//        List<Control> sortedList = controlList.stream()
//                .sorted(comparator)
//                .collect(Collectors.toList());
//
//        //Return a PageImpl containing the sorted list of security compliance
//        return new PageImpl<>(sortedList, pageable, controls.getTotalElements());
//    }
//
//    private int compareValues(Comparable value1, Comparable value2) {
//        //If both value are null, then consider them equal
//        if (value1 == null && value2 == null) return 0;
//
//        //If value1 is null, consider value2 as greater
//        if (value1 == null) return -1;
//
//        //If value2 is null, consider value1 as greater
//        if (value2 == null) return 1;
//
//        //Compare two values
//        return value1.compareTo(value2);
//    }

    //FUNCTION TO CREATE SPECIFICATION FOR DYNAMIC SEARCH FILTER
    private Specification<Control> controlFilter(List<String> controlCategory, List<String> control, Boolean status)
    {
        return (root, query, criteriaBuilder) ->
        {
            List<Predicate> predicates = new ArrayList<>();

            if (control != null && !control.isEmpty()) {
                predicates.add(root.get("controlName").in(control));
                log.info("Added predicate for control: {}", control);
            }

            if (controlCategory != null && !controlCategory.isEmpty()) {
                predicates.add(root.get("controlCategory").get("controlCategoryName").in(controlCategory));
                log.info("Added predicate for control category: {}", controlCategory);
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
                log.info("Added predicate for status: {}", status);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public ByteArrayResource exportExcelControl(ControlFilterDTO filter) throws IOException {
        log.info("Starting export of control data to Excel");


        // Retrieve all control DTOs
        PaginatedResponse<ControlDTO> paginatedResponse = getFilterControlPaginated(filter);
        List<ControlDTO> controlList = paginatedResponse.getContent();
        log.debug("Fetched {} control records for export", controlList.size());

        // Output stream and configuration for Excel export
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        List<String> fieldsToInclude = List.of(
                "controlCategory",
                "controlName",
                "description",
                "status"
        );
        //Custom headers for Excel Columns.
        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("controlCategory", "CONTROL CATEGORY");
        customHeaders.put("controlName", "CONTROL");
        customHeaders.put("description", "DESCRIPTION");
        customHeaders.put("status", "STATUS");

        //Call exportExcel to export data to excel.
        exportExcel.exportToExcel(outputStream, controlList, "control", fieldsToInclude, customHeaders);
        log.info("Successfully exported control data to Excel");

        // Convert Output Stream to ByteArrayResource
        byte[] excelContent = outputStream.toByteArray();
        ByteArrayResource resource = new ByteArrayResource(excelContent);
        return resource;
    }


    //FUNCTION TO PARSE CSV FILE TO DTO
    public List<ControlCreateDTO> parseCSVFile(MultipartFile file) throws IOException {
        List<ControlCreateDTO> controlCreateDTOs = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header row
                }
                String[] values = line.split(",");
                ControlCreateDTO dto = new ControlCreateDTO();

                // Handle the controlId field
                String controlId = values[0].trim();
                if (controlId.isEmpty() || controlId.equals("\"\"")) {
                    dto.setControlId(null);
                } else {
                    dto.setControlId(controlId.replaceAll("^\"|\"$", ""));
                }

                dto.setControlName(removeQuotes(values[1].trim()));
                dto.setDescription(removeQuotes(values[2].trim()));
                dto.setStatus(Boolean.parseBoolean(removeQuotes(values[3].trim())));


                // Handle the controlCategoryName field specifically
                String categoryName = removeQuotes(values[4].trim());
                if (categoryName.endsWith("\"")) {
                    categoryName = categoryName.substring(0, categoryName.length() - 1);
                }
                dto.setControlCategoryName(categoryName);
                controlCreateDTOs.add(dto);
            }
        }

        return controlCreateDTOs;
    }

    //FUNCTION TO REMOVE QUOTATION MARKS
    private String removeQuotes(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
