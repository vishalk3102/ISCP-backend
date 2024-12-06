package com.iscp.backend.services.impl;

import com.iscp.backend.dto.EmployeeDTO;
import com.iscp.backend.mapper.EmployeeMapper;
import com.iscp.backend.mims.model.Employee;
import com.iscp.backend.mims.repositories.EmployeeRepository;
import com.iscp.backend.services.EmployeeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

   private EmployeeRepository employeeRepository;

   private EmployeeMapper employeeMapper;

    @Override
    public List<EmployeeDTO> getAllEmployee() {
        List<Employee> employeeList = employeeRepository.findAll();
        return employeeMapper.toEmployeeDTOList(employeeList);
    }
}
