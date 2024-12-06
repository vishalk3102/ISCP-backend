package com.iscp.backend.services.impl;

import com.iscp.backend.dto.DepartmentDTO;
import com.iscp.backend.mapper.DepartmentMapper;
import com.iscp.backend.models.Department;
import com.iscp.backend.models.Role;
import com.iscp.backend.repositories.DepartmentRepository;
import com.iscp.backend.repositories.UsersRepository;
import com.iscp.backend.services.DepartmentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link DepartmentService} interface for managing departments.
 */
@AllArgsConstructor
@Slf4j
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    private final DepartmentMapper departmentMapper;

    private final UsersRepository usersRepository;

    /**
     * Retrieves a list of all departments from the database.
     *
     * @return a list of {@link DepartmentDTO} containing all departments, or an empty list if no departments are found.
     */
    @Override
    public List<DepartmentDTO> getAllDepartmentNames() {
        //Retrieve List of all Departments
        List<Department> departments = departmentRepository.findAll();

        //Check if the retrieved departments list is empty
        if(departments.isEmpty()) {
            return List.of();
        }
        //Convert List of Department Entity to List of DepartmentDTO
        return departmentMapper.toDtoList(departments);
    }

    /**
     * Retrieves a list of all departments associated to the currently logged-in user.
     *
     * @return a list of {@link DepartmentDTO} representing the departments associated to the currently logged-in user.
     */
    @Override
    public List<DepartmentDTO> getLogInUserDepartments() {
        //Get details of currently Logged-in User
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<Department> userDepartments = new ArrayList<>();

        //Check if there is an Authenticated User
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("No authenticated user found");
        }
        else if (authentication.getPrincipal() instanceof LdapUserDetailsImpl ldapUserDetails) {
            log.info("Authenticated LDAP user: {}", ldapUserDetails.getUsername());

            //Retrieve List of all Departments associated to the logged-in user
            userDepartments = usersRepository.findLogInUserDepartment(ldapUserDetails.getUsername());
            log.info("users departments {}", userDepartments);

            //Check if user has admin role
            List<Role> roles = usersRepository.findAllRolesByUserEmailId(ldapUserDetails.getUsername());
            boolean isAdmin=roles.stream()
                    .anyMatch(role -> "admin".equalsIgnoreCase(String.valueOf(role.getRoleName())));

            //If user is admin, return list of all departments
            if (isAdmin) {
                log.info("User is Admin. Fetching all departments.");
                return getAllDepartmentNames();
            }
            //Return Departments associated to LoggedIn User
            else {
                log.info("User is noy Admin. Returning user's departments.");
                return departmentMapper.toDtoList(userDepartments);
            }
        }
        else {
            log.error("Principal is not of type LdapUserDetailsImpl, but of type: {}", authentication.getPrincipal().getClass().getName());
        }
        return departmentMapper.toDtoList(userDepartments);
    }
}