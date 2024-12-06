package com.iscp.backend.mapper;


import com.iscp.backend.dto.ControlCreateDTO;
import com.iscp.backend.dto.ControlDTO;
import com.iscp.backend.dto.ControlUpdateDTO;
import com.iscp.backend.models.Control;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Optional;


@Mapper(componentModel = "spring")
public interface ControlMapper {

    //Mapping from Control entity to controlDTO
    @Mapping(target = "controlCategory.controlCategoryName", source = "controlCategory.controlCategoryName")
    ControlDTO toControlDTO(Control control);

    //Mapping from controlCreateDTO to control entity
    @Mapping(target = "controlCategory.controlCategoryName",source = "controlCategoryName")
    @Mapping(target = "controlId", ignore = true)
    Control toControlEntity(ControlCreateDTO controlCreateDTO);


    // Update existing Control entity from ControlUpdateDTO
    @Mapping(target = "controlId",ignore = true)
//    @Mapping(target = "controlCategory",ignore = true)
    void updateControlEntityFromDTO(ControlUpdateDTO controlUpdateDTO, @MappingTarget  Control control);


    ControlUpdateDTO toControlUpdateDTO(ControlCreateDTO controlCreateDTO);


    //Mapping from List of control entities to list of controlDTO
    List<ControlDTO> toControlDTOs(List<Control> controls);
}
