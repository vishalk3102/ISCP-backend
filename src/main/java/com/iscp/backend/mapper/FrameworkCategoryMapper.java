package com.iscp.backend.mapper;

import com.iscp.backend.dto.*;
import com.iscp.backend.models.FrameworkCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper interface for mapping between Framework Category entities and FrameworkCategory DTO.
 */
@Mapper(componentModel = "spring")
public interface FrameworkCategoryMapper {

    FrameworkCategoryMapper INSTANCE = Mappers.getMapper(FrameworkCategoryMapper.class);

    /**
     * Mapping FrameworkCategoryCreateDTO to FrameworkCategory Entity.
     *
     * @param frameworkCategoryCreateDTO the FrameworkCategoryCreateDTO to convert.
     * @return the converted FrameworkCategory Entity.
     */
    @Mapping(target = "frameworkCategoryId", ignore = true)
    FrameworkCategory toFrameworkCategoryEntity(FrameworkCategoryCreateDTO frameworkCategoryCreateDTO);


    /**
     * Mapping FrameworkCategory Entity to FrameworkCategoryDTO.
     *
     * @param frameworkCategory the FrameworkCategory Entity to convert.
     * @return the converted FrameworkCategoryDTO.
     */
    FrameworkCategoryDTO toFrameworkCategoryDTO(FrameworkCategory frameworkCategory);


    /**
     * Mapping List of FrameworkCategory Entity to List of FrameworkCategoryDTO.
     *
     * @param frameworkCategoryList the list of FrameworkCategory Entity to convert.
     * @return the converted list of FrameworkCategoryDTO.
     */
    List<FrameworkCategoryDTO> toFrameworkCategoryDTO(List<FrameworkCategory> frameworkCategoryList);


    /**
     * Mapping FrameworkCategoryUpdateDTO to FrameworkCategory Entity.
     *
     * @param frameworkCategoryUpdateDTO the FrameworkCategoryUpdateDTO to convert.
     * @param frameworkCategory the converted FrameworkCategory Entity.
     */
    @Mapping(target = "frameworkCategoryId",ignore = true)
    void updateFrameworkCategoryEntityFromDTO(FrameworkCategoryUpdateDTO frameworkCategoryUpdateDTO, @MappingTarget FrameworkCategory frameworkCategory);
}
