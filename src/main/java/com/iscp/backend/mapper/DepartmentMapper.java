package com.iscp.backend.mapper;

import com.iscp.backend.dto.DepartmentDTO;
import com.iscp.backend.models.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper interface for mapping between Department entities and DepartmentDTO.
 */
@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    /**
     * Mapping DepartmentDTO to Department Entity.
     *
     * @param departmentDTO the Department DTO to convert.
     * @return the converted Department Entity.
     */
    Department toEntity(DepartmentDTO departmentDTO);


    /**
     * Mapping Department Entity to DepartmentDTO.
     *
     * @param department the Department Entity to convert.
     * @return the converted DepartmentDTO.
     */
    @Mapping(source = "departmentName", target = "departmentName")
    DepartmentDTO toDto(Department department);


    /**
     * Mapping list of Department Entity to list of DepartmentDTO.
     *
     * @param departments the list of Department Entity to convert.
     * @return the list of converted DepartmentDTO.
     */
    List<DepartmentDTO> toDtoList(List<Department> departments);
}
