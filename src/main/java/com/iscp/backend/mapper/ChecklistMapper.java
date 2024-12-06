package com.iscp.backend.mapper;


import com.iscp.backend.dto.*;
import com.iscp.backend.models.Checklist;
import com.iscp.backend.models.Control;
import com.iscp.backend.models.Framework;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ChecklistMapper {

    //Mapping from checklist entity to checklistDTO
    @Mapping(target = "controlName", source = "control", qualifiedByName = "mapControlEntityToName")
    ChecklistDTO toChecklistDTO(Checklist checklist);

    //Mapping from checkCreateDTO to checklist entity
    @Mapping(target = "checklistId", ignore = true)
    @Mapping(target = "control", ignore = true)
    Checklist toChecklistEntity(ChecklistCreateDTO checklistCreateDTO);


    // Update existing checklist entity from checklistUpdateDTO
    @Mapping(target = "checklistId",ignore = true)
    @Mapping(target = "control", ignore = true)
    void updateChecklistEntityFromDTO(ChecklistUpdateDTO checklistUpdateDTO, @MappingTarget Checklist checklist);

    //Mapping from List of checklist entities to list of checklistDTO
    @Mapping(target = "controlName", source = "control", qualifiedByName = "mapControlEntityToName")
    List<ChecklistDTO> toChecklistDTOs(List<Checklist> checklists);


    //Mapping from checklistCreateDTO to checklistUpdateDTO entity
    ChecklistUpdateDTO toChecklistUpdateDTO(ChecklistCreateDTO checklistCreateDTO);

    @Named("mapControlEntityToName")
    default String mapControlEntityToName(Control control) {
        return control != null ? control.getControlName() : null;
    }
}
