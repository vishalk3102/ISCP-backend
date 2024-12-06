package com.iscp.backend.mapper;

import com.iscp.backend.dto.EmployeeDTO;
import com.iscp.backend.mims.model.Employee;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    List<EmployeeDTO> toEmployeeDTOList(List<Employee> employees);
}
