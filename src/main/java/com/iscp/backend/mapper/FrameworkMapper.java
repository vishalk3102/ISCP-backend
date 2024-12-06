package com.iscp.backend.mapper;

import com.iscp.backend.dto.*;
import com.iscp.backend.models.Framework;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Mapper interface for mapping between Framework entities and FrameworkDTO.
 */
@Mapper(componentModel = "spring")
public interface FrameworkMapper {

    /**
     * Mapping FrameworkCreateDTO to Framework Entity.
     *
     * @param frameworkCreateDTO the FrameworkCreateDTO to convert.
     * @return the converted Framework Entity.
     */
    @Mapping(target = "frameworkId", ignore = true)
    @Mapping(target = "frameworkCategory", ignore = true)
    Framework toFrameworkEntity(FrameworkCreateDTO frameworkCreateDTO);


    /**
     * Mapping Framework Entity to FrameworkDTO.
     *
     * @param framework the Framework Entity to convert.
     * @return the converted FrameworkDTO.
     */
    FrameworkDTO toFrameworkDTO(Framework framework);


    /**
     * Mapping List of Framework Entity to List of FrameworkDTO.
     *
     * @param frameworks the list of Framework Entity to convert.
     * @return the list of converted FrameworkDTO.
     */
    List<FrameworkDTO> toFrameworkDTO(List<Framework> frameworks);


    /**
     * Mapping FrameworkUpdateDTO to Framework Entity.
     *
     * @param frameworkUpdateDTO the FrameworkUpdateDTO to convert.
     * @param framework the converted Framework Entity.
     */
    @Mapping(target = "frameworkId",ignore = true)
    void updateFrameworkEntityFromDTO(FrameworkUpdateDTO frameworkUpdateDTO, @MappingTarget Framework framework);


    /**
     * Mapping FrameworkCreateDTO to FrameworkUpdateDTO.
     *
     * @param frameworkCreateDTO the FrameworkCreateDTO to convert.
     * @return the converted FrameworkUpdateDTO.
     */
    FrameworkUpdateDTO toFrameworkUpdateDTO(FrameworkCreateDTO frameworkCreateDTO);

}
