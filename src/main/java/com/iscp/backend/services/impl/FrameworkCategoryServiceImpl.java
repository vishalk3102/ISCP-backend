package com.iscp.backend.services.impl;

import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.FrameworkCategoryExistsException;
import com.iscp.backend.exceptions.FrameworkCategoryNotFoundException;
import com.iscp.backend.mapper.FrameworkCategoryMapper;
import com.iscp.backend.models.FrameworkCategory;
import com.iscp.backend.repositories.FrameworkCategoryRepository;
import com.iscp.backend.services.FrameworkCategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class FrameworkCategoryServiceImpl implements FrameworkCategoryService {

    private final FrameworkCategoryMapper frameworkCategoryMapper;

    private final FrameworkCategoryRepository frameworkCategoryRepository;

    /**
     * Adds a new framework category.
     *
     * @param frameworkCategoryCreateDTO Data Transfer Object containing details of the framework category to be created.
     * @return The created FrameworkCategoryDTO.
     * @throws FrameworkCategoryExistsException if a framework category with the same name already exists.
     */
    @Override
    public FrameworkCategoryDTO addFrameworkCategory(FrameworkCategoryCreateDTO frameworkCategoryCreateDTO) throws FrameworkCategoryExistsException {

        log.debug("Attempting to add framework category: {}", frameworkCategoryCreateDTO);

        // Check for Framework category is present or not
        Optional<FrameworkCategory> optionalFrameworkCategory = frameworkCategoryRepository.findByFrameworkCategoryName(frameworkCategoryCreateDTO.getFrameworkCategoryName());

        if (optionalFrameworkCategory.isPresent())
        {
            log.error("Error in updating a framework category: Framework Category with name  {} already exists", frameworkCategoryCreateDTO.getFrameworkCategoryName());
            throw new FrameworkCategoryExistsException();
        }

        FrameworkCategory frameworkCategory =  frameworkCategoryMapper.toFrameworkCategoryEntity(frameworkCategoryCreateDTO);
        FrameworkCategory savedframeworkCategory =  frameworkCategoryRepository.save(frameworkCategory);
        log.info("Framework Category added successfully with id: {}", savedframeworkCategory.getFrameworkCategoryId());

        return  frameworkCategoryMapper.toFrameworkCategoryDTO(savedframeworkCategory);
    }


    /**
     * Updates multiple framework categories.
     *
     * @param frameworkCategoryUpdateDTOs List of Data Transfer Objects containing updated details of framework categories.
     * @return List of updated FrameworkCategoryDTOs.
     * @throws FrameworkCategoryNotFoundException if any of the framework categories to be updated are not found.
     * @throws FrameworkCategoryExistsException if any of the updated framework categories already exist with the new name.
     */
    @Override
    public List<FrameworkCategoryDTO> updateFrameworkCategory(List<FrameworkCategoryUpdateDTO> frameworkCategoryUpdateDTOs) throws FrameworkCategoryNotFoundException, FrameworkCategoryExistsException {
        log.debug("Attempting to update multiple frameworks category");
        List<FrameworkCategory> updatedFrameworkCategoryList = new ArrayList<>();
        for(FrameworkCategoryUpdateDTO updateCategoryDTO : frameworkCategoryUpdateDTOs)
        {
            // Check for Framework category is present or not
            Optional<FrameworkCategory> optionalFrameworkCategory = frameworkCategoryRepository.findById(updateCategoryDTO.getFrameworkCategoryId());

            // If the framework category is not found, throw an exception
            if (optionalFrameworkCategory.isEmpty())
            {
                log.error("Error in updating a framework category: Framework Category with id {} not found", updateCategoryDTO.getFrameworkCategoryId());
                throw new FrameworkCategoryNotFoundException();
            }

            if(Objects.equals(optionalFrameworkCategory.get().getFrameworkCategoryName(), updateCategoryDTO.getFrameworkCategoryName()))
            {
                log.error("Error in updating a framework category: Framework Category Already exists {} ", updateCategoryDTO.getFrameworkCategoryName());
                throw new FrameworkCategoryExistsException();
            }

            frameworkCategoryMapper.updateFrameworkCategoryEntityFromDTO(updateCategoryDTO,optionalFrameworkCategory.get());
            FrameworkCategory apply = optionalFrameworkCategory.get();
            updatedFrameworkCategoryList.add(apply);
        }

        List<FrameworkCategory> updatedFrameworkCategoryEntity  =  frameworkCategoryRepository.saveAll(updatedFrameworkCategoryList);

        List<FrameworkCategoryDTO> updatedFrameworkCategoryDTO = updatedFrameworkCategoryEntity.stream()
                .map(frameworkCategoryMapper::toFrameworkCategoryDTO)
                .toList();

        log.info("Multiple framework Category updated successfully");

        return updatedFrameworkCategoryDTO;
    }

    /**
     * Retrieves all framework categories.
     *
     * @return List of all FrameworkCategoryDTOs.
     */
    @Override
    public List<FrameworkCategoryDTO> getAllFrameworkCategories() {
        log.debug("Attempting to get all  framework categories");

        List<FrameworkCategory> frameworkCategories=frameworkCategoryRepository.findAll(Sort.by(Sort.Direction.ASC,"frameworkCategoryName"));

        if (frameworkCategories.isEmpty())
        {
            log.info("No framework categories found");
            return List.of();
        }
        log.info("Fetched framework categories :{}",frameworkCategories);
        return  frameworkCategoryMapper.toFrameworkCategoryDTO(frameworkCategories);
    }
}
