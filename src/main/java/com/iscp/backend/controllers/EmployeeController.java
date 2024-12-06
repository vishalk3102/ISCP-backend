package com.iscp.backend.controllers;

import com.iscp.backend.dto.EmployeeDTO;
import com.iscp.backend.services.EmployeeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = EmployeeController.PATH,produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
public class EmployeeController {

    public final static String PATH = "/api/employee";

    private final EmployeeService employeeService;

    @GetMapping("/get-all-employee")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployee()
    {
        List<EmployeeDTO> employeeDTOList = employeeService.getAllEmployee();

        return ResponseEntity.status(HttpStatus.OK).body(employeeDTOList);
    }
}
