package com.iscp.backend.services.impl;

import com.iscp.backend.components.ExportExcel;
import com.iscp.backend.components.Pagination;
import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.FrameworkCategoryNotFoundException;
import com.iscp.backend.exceptions.FrameworkNotFoundException;
import com.iscp.backend.models.Framework;
import com.iscp.backend.mapper.FrameworkMapper;
import com.iscp.backend.models.FrameworkCategory;
import com.iscp.backend.repositories.FrameworkCategoryRepository;
import com.iscp.backend.repositories.FrameworkRepository;
import com.iscp.backend.services.FrameworkService;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Slf4j
public class FrameworkServiceImpl implements FrameworkService {

    private final FrameworkMapper frameworkMapper;

    private final FrameworkRepository frameworkRepository;

    private final FrameworkCategoryRepository frameworkCategoryRepository;

    private final ExportExcel exportExcel;


    //FUNCTION TO ADD/UPDATE FRAMEWORK
    @Override
    public List<FrameworkDTO> addEditFramework(List<FrameworkCreateDTO> frameworkCreateDTOList) throws FrameworkCategoryNotFoundException, FrameworkNotFoundException {

        List<FrameworkUpdateDTO> frameworkUpdateDTOs = new ArrayList<>();
        List<FrameworkDTO> frameworkDTOList = new ArrayList<>();


        for(FrameworkCreateDTO frameworkDTO :frameworkCreateDTOList)
        {
            String id = frameworkDTO.getFrameworkId();
            if(id.isBlank())
            {
                frameworkDTOList.add(addFramework(frameworkDTO));
            }
            else if(frameworkRepository.findByFrameworkId(id).isEmpty())
            {
                throw new FrameworkNotFoundException();
            }
            else
            {
                frameworkUpdateDTOs.add(frameworkMapper.toFrameworkUpdateDTO(frameworkDTO));
            }

        }

        frameworkDTOList.addAll(updateFramework(frameworkUpdateDTOs));
        return  frameworkDTOList;
    }


    /**
     * Adds a new framework.
     *
     * @param frameworkCreateDTO Data Transfer Object containing details of the framework to be created.
     * @return The created FrameworkDTO.
     * @throws FrameworkCategoryNotFoundException if the framework category specified in the DTO is not found.
     */
    @Override
    public FrameworkDTO addFramework(FrameworkCreateDTO frameworkCreateDTO) throws FrameworkCategoryNotFoundException {
        log.debug("Attempting to add framework: {}", frameworkCreateDTO);
        // Find the associated Framework Category

        Optional<FrameworkCategory> frameworkCategory =frameworkCategoryRepository.findByFrameworkCategoryName(frameworkCreateDTO.getFrameworkCategoryName());
        if(frameworkCategory.isEmpty()) {
            log.error("Error adding framework: framework Category not found");
            throw new FrameworkCategoryNotFoundException();
        }
        // Create and save new Framework
        Framework framework = frameworkMapper.toFrameworkEntity(frameworkCreateDTO);
        framework.setFrameworkCategory(frameworkCategory.get());
        framework.setCreationTime(LocalDateTime.now());
        Framework savedFramework = frameworkRepository.save(framework);
        log.info("Framework added successfully with id: {}", savedFramework.getFrameworkId());
        return frameworkMapper.toFrameworkDTO(savedFramework);
    }

    /**
     * Updates multiple frameworks based on the provided list of update DTOs.
     *
     * @param frameworkUpdateDTOs List of Data Transfer Objects containing updated details of frameworks.
     * @return List of updated FrameworkDTOs.
     * @throws FrameworkNotFoundException if any of the frameworks to be updated are not found.
     * @throws FrameworkCategoryNotFoundException if the framework category specified in the DTO is not found.
     */
    @Override
    public List<FrameworkDTO> updateFramework(List<FrameworkUpdateDTO> frameworkUpdateDTOs) throws FrameworkNotFoundException, FrameworkCategoryNotFoundException {

        log.debug("Attempting to update multiple frameworks");
        List<Framework> updatedFrameworksList = new ArrayList<>();
        for(FrameworkUpdateDTO updateDTO : frameworkUpdateDTOs)
        {
            // Check for Framework is present or not
            Optional<Framework> optionalFramework = frameworkRepository.findById(updateDTO.getFrameworkId());

            // If the framework is not found, throw an exception
            if (optionalFramework.isEmpty())
            {
                log.error("Error in updating a framework: Framework with id {} not found", updateDTO.getFrameworkId());
                throw new FrameworkNotFoundException();
            }

            // Check for Framework category is present or not
            Optional<FrameworkCategory> frameworkCategory = frameworkCategoryRepository.findByFrameworkCategoryName(updateDTO.getFrameworkCategoryName());

            if (frameworkCategory.isEmpty())
            {
                log.error("Error in updating a framework: Framework Category with id {} not found", updateDTO.getFrameworkCategoryName());
                throw new FrameworkCategoryNotFoundException();
            }

            frameworkMapper.updateFrameworkEntityFromDTO(updateDTO, optionalFramework.get());
            optionalFramework.get().setFrameworkCategory(frameworkCategory.get());
            Framework apply = optionalFramework.get();
            apply.setCreationTime(LocalDateTime.now());
            updatedFrameworksList.add(apply);
        }

        List<Framework> updatedFrameworksEntity  =  frameworkRepository.saveAll(updatedFrameworksList);

        List<FrameworkDTO> updatedFrameworksDTO = updatedFrameworksEntity.stream()
                .map(frameworkMapper::toFrameworkDTO)
                .toList();

        log.info("Multiple frameworks updated successfully");

        return updatedFrameworksDTO;

    }

    /**
     * Retrieves all frameworks from the system.
     *
     * @return List of all FrameworkDTOs.
     */
    @Override
    public List<FrameworkDTO> getAllFrameworks() {

        log.debug("Attempting to get all frameworks");
        List<Framework> frameworksEntity = frameworkRepository.findAll(Sort.by(Sort.Direction.ASC,"frameworkName"));

        if (frameworksEntity.isEmpty())
        {
            log.info("No Framework Found found");
            return List.of();
        }
        log.info("Successfully fetched all the frameworks {} ", frameworksEntity);

        return frameworkMapper.toFrameworkDTO(frameworksEntity);
    }


    // GET FRAMEWORK FROM FRAMEWORK CATEGORY
    @Override
    public List<FrameworkDTO> getFrameworkFromFrameworkCategory(String frameworkCategoryName) throws FrameworkCategoryNotFoundException {

        log.debug("Attempting to get all  frameworks");

        // Retrieve the framework category  from the repository.
        Optional<FrameworkCategory> frameworkCategory=frameworkCategoryRepository.findByFrameworkCategoryName(frameworkCategoryName);
        if(frameworkCategory.isEmpty())
        {
            log.warn("Framework Category with name {}  not found",frameworkCategoryName);
            throw  new FrameworkCategoryNotFoundException();
        }

        // Retrieve all frameworks for a specific frameworkCategory from the repository
        List<Framework> frameworks=frameworkRepository.findByFrameworkCategory_FrameworkCategoryName(frameworkCategoryName,Sort.by(Sort.Direction.ASC, "frameworkName"));
        if(frameworks.isEmpty())
        {
            log.info("No framework found");
            return List.of();
        }
        log.info("Fetched frameworks :{}",frameworks);
        return  frameworkMapper.toFrameworkDTO(frameworks);
    }

    @Override
    public ByteArrayResource exportExcelFramework(FrameworkFilterDTO filter) throws IOException {
        log.info("Starting export of framework data to Excel");

        // Retrieve all Framework DTOs
        PaginatedResponse<FrameworkDTO> paginatedResponse = getFilterFrameworkPaginated(filter);
        List<FrameworkDTO> frameworksList=paginatedResponse.getContent();
        log.debug("Fetched {} framework records for export", frameworksList.size());

        for (FrameworkDTO frameworkDTO : frameworksList) {
            // Set the complianceCalendar field by concatenating startDate and endDate
            frameworkDTO.setStartDate(filter.getStartDate()+ " - " + filter.getEndDate());
        }
        // Output stream and configuration for Excel export
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        List<String> fieldsToInclude = List.of(
                "frameworkCategory",
                "frameworkName",
                "startDate",
                "description",
                "status"
        );
        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("frameworkCategory", "FRAMEWORK CATEGORY");
        customHeaders.put("frameworkName", "FRAMEWORK");
        customHeaders.put("startDate", "COMPLIANCE CALENDAR");
        customHeaders.put("description", "DESCRIPTION");
        customHeaders.put("status", "STATUS");

        // Use the existing export method
        exportExcel.exportToExcel(outputStream, frameworksList, "framework", fieldsToInclude, customHeaders);
        log.info("Successfully exported framework data to Excel");

        // Convert Output Stream to ByteArrayResource
        byte[] excelContent = outputStream.toByteArray();
        ByteArrayResource resource = new ByteArrayResource(excelContent);
        return resource;
    }




    //FUNCTION FOR SEARCH FUNCTIONALITY AND PAGINATION
    @Override
    public PaginatedResponse<FrameworkDTO> getFilterFrameworkPaginated(FrameworkFilterDTO filterDTO) {

        Specification<Framework> spec =  frameworkFilter(filterDTO.getFrameworkCategory(), filterDTO.getFramework(), filterDTO.getStatus(), filterDTO.getStartDate(), filterDTO.getEndDate());

        Pageable pageable= Pagination.createPageable(filterDTO.getPage(), filterDTO.getSize(),filterDTO.getSortField(),filterDTO.getSortOrder());

        Page<Framework> securityComplianceList = frameworkRepository.findAll(spec,pageable);

        Page<FrameworkDTO> frameworkDTOPage  =securityComplianceList.map(frameworkMapper::toFrameworkDTO);

        return  Pagination.createdPaginatedContent(frameworkDTOPage);
    }

    //FUNCTION TO CREATE SPECIFICATION FOR SEARCH
    private Specification<Framework> frameworkFilter(List<String> frameworkCategory, List<String> frameworks, Boolean status, String startDate, String endDate)
    {
        return (root, query, criteriaBuilder) ->
        {
            List<Predicate> predicates = new ArrayList<>();

            if (frameworkCategory != null && !frameworkCategory.isEmpty()) {
                predicates.add(root.get("frameworkCategory").get("frameworkCategoryName").in(frameworkCategory));
                log.info("Added predicate for frameworkCategory: {}", frameworkCategory);
            }

            // Frameworks Filter
            if (frameworks != null && !frameworks.isEmpty()) {

                predicates.add(root.get("frameworkName").in(frameworks));
                log.info("Added predicate for frameworkName: {}", frameworks);
            }

            if(status!=null)
            {
                predicates.add(criteriaBuilder.equal(root.get("status"),status));
                log.info("Added predicate for status: {}", status);
            }

            if(startDate !=null && !startDate.isEmpty())
            {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"),startDate));
                log.info("Added predicate for framework start date: {}", startDate);
            }

            if(endDate != null  && !endDate.isEmpty())
            {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endDate"),endDate));
                log.info("Added predicate for framework end date: {}", endDate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }
}

