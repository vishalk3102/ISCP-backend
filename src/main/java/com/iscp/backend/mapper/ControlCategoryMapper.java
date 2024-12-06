package com.iscp.backend.mapper;


import com.iscp.backend.dto.ControlCategoryCreateDTO;
import com.iscp.backend.dto.ControlCategoryDTO;
import com.iscp.backend.dto.ControlCategoryUpdateDTO;
import com.iscp.backend.dto.ControlDTO;
import com.iscp.backend.models.Control;
import com.iscp.backend.models.ControlCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ControlCategoryMapper {
    //Mapping from controlCategoryDTO to controlCategory entity
    @Mapping(target = "controlList",ignore = true)
    ControlCategory toControlCategoryEntity(ControlCategoryDTO controlCategoryDTO);

    //Mapping from controlCategory entity to controlCategoryDTO
    ControlCategoryDTO toControlCategoryDTO(ControlCategory controlCategory);

    //Mapping from controlCategoryCreateDTO to controlCategory entity
    @Mapping(target = "controlCategoryId",ignore = true)
    ControlCategory toControlCategoryEntity(ControlCategoryCreateDTO controlCategoryCreateDTO);


    // Update existing Control entity from ControlUpdateDTO
    @Mapping(target = "controlCategoryId",ignore = true)
    @Mapping(target = "controlList",ignore = true)
    void updateControlCategoryEntityFromDTO(ControlCategoryUpdateDTO controlCategoryUpdateDTO, @MappingTarget ControlCategory controlCategory);

    //Mapping from List of control category entities to list of controlCategoryDTO
    List<ControlCategoryDTO> toControlCategoryDTOs(List<ControlCategory> controlCategories);

    ControlCategoryUpdateDTO toControlCategoryUpdateDTO(ControlCategoryCreateDTO controlCategoryDTO);
}
