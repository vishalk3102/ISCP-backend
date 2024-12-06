package com.iscp.backend.services.impl;

import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.ChecklistNotFoundException;
import com.iscp.backend.exceptions.ControlCategoryAlreadyExistsException;
import com.iscp.backend.exceptions.ControlCategoryNotFoundException;
import com.iscp.backend.exceptions.ControlNotFoundException;
import com.iscp.backend.mapper.ControlCategoryMapper;
import com.iscp.backend.models.Checklist;
import com.iscp.backend.models.Control;
import com.iscp.backend.models.ControlCategory;
import com.iscp.backend.repositories.ControlCategoryRepository;
import com.iscp.backend.services.ControlCategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ControlCategoryServiceImpl implements ControlCategoryService {

    private final ControlCategoryRepository controlCategoryRepository;
    private final ControlCategoryMapper controlCategoryMapper;



    //FUNCTION TO GET ALL CONTROL CATEGORY
    @Override
    public List<ControlCategoryDTO> getAllControlCategories() {
        log.debug("Attempting to get all  control categories");

        // Retrieve all control categories from the repository
        List<ControlCategory> controlCategories=controlCategoryRepository.findAll(Sort.by(Sort.Direction.ASC,"controlCategoryName"));

        // Check if the retrieved list is empty
        if (controlCategories.isEmpty())
        {
            log.info("No control categories found");
            return List.of();
        }
        log.info("Fetched control categories :{}",controlCategories);
        return  controlCategoryMapper.toControlCategoryDTOs(controlCategories);
    }


    //FUNCTION TO GET CONTROL CATEGORY  BY ID
    @Override
    public ControlCategoryDTO getControlCategoryById(String id) throws  ControlCategoryNotFoundException{
        log.debug("Fetching control Category with id: {}", id);
        Optional<ControlCategory> controlCategory=controlCategoryRepository.findById(id);
        if (controlCategory.isEmpty())
        {
            throw  new ControlCategoryNotFoundException();
        }
        return controlCategoryMapper.toControlCategoryDTO(controlCategory.get());
    }

    //FUNCTION TO ADD CONTROL CATEGORY
    @Override
    public ControlCategoryDTO addControlCategory(ControlCategoryCreateDTO controlCategoryCreateDTO) throws ControlCategoryAlreadyExistsException {
        log.debug("Attempting to add Control: {}", controlCategoryCreateDTO);

        // Check if a control category with the same name already exists
        Optional<ControlCategory> existingControlCategory=controlCategoryRepository.findByControlCategoryName(controlCategoryCreateDTO.getControlCategoryName());
        if(existingControlCategory.isPresent())
        {
            log.warn("Control category with name {}  already exists",controlCategoryCreateDTO.getControlCategoryName());
            throw  new ControlCategoryAlreadyExistsException();
        }

        // Create and save the new Control Category
        ControlCategory controlCategory= controlCategoryMapper.toControlCategoryEntity(controlCategoryCreateDTO);
        ControlCategory savedControlCategory= controlCategoryRepository.save(controlCategory);

        log.info("Control added successfully with id: {}", savedControlCategory.getControlCategoryId());
        return controlCategoryMapper.toControlCategoryDTO(savedControlCategory);
    }

    //FUNCTION TO UPDATE ALL EXISTING  CONTROL CATEGORIES
    @Override
    public List<ControlCategoryDTO> updateControlCategories(List<ControlCategoryUpdateDTO> controlCategoryUpdateDTOS) {
        log.debug("Attempting to update control categories");

        List<ControlCategory> updatedControlCategories=controlCategoryUpdateDTOS.stream().map(controlCategoryUpdateDTO -> {
            try {
                return  updateSingleControlCategory(controlCategoryUpdateDTO);
            }
            catch (ControlCategoryNotFoundException e)
            {
                log.error("Control Category  not found with id {}",controlCategoryUpdateDTO.getControlCategoryId());
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());


        // Update and save all the checklist
        List<ControlCategory> savedChecklist=controlCategoryRepository.saveAll(updatedControlCategories);
        log.info("Updated {} checklist successfully ", savedChecklist.size());


        return  savedChecklist.stream().map(controlCategoryMapper::toControlCategoryDTO).collect(Collectors.toList());
    }


    //FUNCTION TO UPDATE SINGLE EXISTING CONTROL CATEGORIES
    @Override
    public ControlCategory updateSingleControlCategory(ControlCategoryUpdateDTO controlCategoryUpdateDTO) throws ControlCategoryNotFoundException {
        log.debug("Attempting to update Checklist with id: {}", controlCategoryUpdateDTO.getControlCategoryId());

        // Find the existing Control Category
        Optional<ControlCategory> controlCategory=controlCategoryRepository.findById(controlCategoryUpdateDTO.getControlCategoryId());
        if(controlCategory.isEmpty()) {
            throw new ControlCategoryNotFoundException();
        }

        // Map controlCategoryupdateDto to controlCategory entity
        controlCategoryMapper.updateControlCategoryEntityFromDTO(controlCategoryUpdateDTO,controlCategory.get());
        return controlCategory.get();
    }

    @Override
    public List<ControlCategoryDTO> addEditControlCategory(List<ControlCategoryCreateDTO> controlCategoryCreateDTOList) throws ControlCategoryAlreadyExistsException, ControlCategoryNotFoundException {
        log.debug("Attempting to add/edit {} controls", controlCategoryCreateDTOList.size());

        List<ControlCategoryUpdateDTO> controlCategoryUpdateDTOS = new ArrayList<>();
        List<ControlCategoryDTO> controlCategoryDTOList = new ArrayList<>();


        for(ControlCategoryCreateDTO controlCategoryDTO :controlCategoryCreateDTOList)
        {
            String id = controlCategoryDTO.getControlCategoryId();
            if(id == null || id.isBlank())
            {
                log.debug("Adding new control with empty ID");
                controlCategoryDTOList.add(addControlCategory(controlCategoryDTO));
            }
            else if(controlCategoryRepository.findByControlCategoryId(id).isEmpty())
            {
                log.error("Control not found with id {}", id);
                throw new ControlCategoryNotFoundException();
            }
            else
            {
                log.debug("Preparing to update control with ID: {}", id);
                controlCategoryUpdateDTOS.add(controlCategoryMapper.toControlCategoryUpdateDTO(controlCategoryDTO));
            }

        }

        List<ControlCategoryDTO> updatedControls = updateControlCategories(controlCategoryUpdateDTOS);
        controlCategoryDTOList.addAll(updatedControls);

        log.info("Successfully processed {} controls", controlCategoryDTOList.size());
        return  controlCategoryDTOList;
    }

    @Override
    public List<ControlCategoryCreateDTO> parseCSVFile(MultipartFile file) throws IOException {
        List<ControlCategoryCreateDTO> controlCategoryCreateDTOS = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header row
                }
                String[] values = line.split(",");
                ControlCategoryCreateDTO dto = new ControlCategoryCreateDTO();

                // Handle the controlId field
                String controlCategoryId = values[0].trim();
                if (controlCategoryId.isEmpty() || controlCategoryId.equals("\"\"")) {
                    dto.setControlCategoryId(null);
                } else {
                    dto.setControlCategoryId(controlCategoryId.replaceAll("^\"|\"$", ""));
                }

                dto.setControlCategoryName(removeQuotes(values[1].trim()));

                controlCategoryCreateDTOS.add(dto);
            }
        }

        return controlCategoryCreateDTOS;
    }

    //FUNCTION TO REMOVE QUOTATION MARKS
    private String removeQuotes(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }


}
