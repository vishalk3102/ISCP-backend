package com.iscp.backend.services;

import com.iscp.backend.dto.DepartmentDTO;

import java.util.List;

/**
 * Service Interface for managing Departments.
 */
public interface DepartmentService {

    /**
     * Retrieves a list of all departments from the database.
     *
     * @return a list of {@link DepartmentDTO} containing all departments, or an empty list if no departments are found.
     */
    List<DepartmentDTO> getAllDepartmentNames();

    /**
     * Retrieves a list of all departments associated to the currently logged-in user.
     *
     * @return a list of {@link DepartmentDTO} representing the departments associated to the currently logged-in user.
     */
    List<DepartmentDTO> getLogInUserDepartments();
}
