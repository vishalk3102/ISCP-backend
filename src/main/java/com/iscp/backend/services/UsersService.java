package com.iscp.backend.services;

import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.*;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.util.List;

/**
 * Service Interface for managing Users.
 */
public interface UsersService{

    /**
     * Retrieves a list of all users from the database.
     *
     * @return a list of {@link UsersDTO} containing all users, or an empty list if no user are found.
     */
    List<UsersDTO> getAllUsers();


    /**
     * Add a list of users based on the provided list of UsersDTO.
     *
     * @param createUsersDTO a list of CreateUsersDTO containing user details to be added.
     * @return a list of {@link UsersDTO} containing added users.
     * @throws DepartmentNotFoundException if any of the specified department does not exist.
     * @throws RoleNotFoundException if any of the specified role does not exist.
     * @throws UserEmailAlreadyExistsException if the specified EmailId already exists.
     * @throws UserEmpCodeAlreadyExistsException if the specified EmpCode already exists.
     */
    UsersDTO addUser(CreateUsersDTO createUsersDTO) throws DepartmentNotFoundException, RoleNotFoundException, UserEmailAlreadyExistsException, UserEmpCodeAlreadyExistsException;


    /**
     * Update a list of users based on the provided list of UsersDTO.
     *
     * @param updateUserDtoList a list of UpdateUserDTO containing user details to be updated.
     * @return a list of {@link UsersDTO} containing updated users.
     * @throws UserNotFoundException if a user ID is not found in the repository.
     * @throws DepartmentNotFoundException if any of the specified department does not exist.
     * @throws RoleNotFoundException if any of the specified role does not exist.
     * @throws UserEmailAlreadyExistsException if the specified EmailId already exists.
     * @throws UserEmpCodeAlreadyExistsException if the specified EmpCode already exists.
     */
    List<UsersDTO> updateUser(List<UpdateUserDTO> updateUserDtoList) throws UserNotFoundException, DepartmentNotFoundException, RoleNotFoundException, UserEmailAlreadyExistsException, UserEmpCodeAlreadyExistsException;


    /**
     * Add or update a list of security compliance based on the provided list of security compliance DTO.
     *
     * @param createUsersDTOList a list of CreateUsersDTO containing details of users to be added or updated.
     * @return a list of {@link UsersDTO} containing added or updated users.
     * @throws UserNotFoundException if a user ID is not found in the repository.
     * @throws DepartmentNotFoundException if any of the specified department does not exist.
     * @throws RoleNotFoundException if any of the specified role does not exist.
     * @throws UserEmailAlreadyExistsException if the specified EmailId already exists.
     * @throws UserEmpCodeAlreadyExistsException if the specified EmpCode already exists.
     */
    List<UsersDTO> addUpdateUsers(List<CreateUsersDTO> createUsersDTOList) throws UserNotFoundException, DepartmentNotFoundException, RoleNotFoundException, UserEmailAlreadyExistsException, UserEmpCodeAlreadyExistsException;


    /**
     * Retrieves a paginated list of filtered users records.
     *
     * @param userFilter UserFilterDTO containing the filtering criteria to be encapsulated.
     * @return a PaginatedResponse containing UsersDTO objects.
     */
    PaginatedResponse<UsersDTO> getFilterUsersPaginated(UserFilterDTO userFilter);


    /**
     * Exports user records to an Excel.
     *
     * @param filter UserFilterDTO containing the filtering criteria to be encapsulated.
     * @return a ByteArrayResource containing the Excel file data.
     * @throws IOException if an error occurs during export.
     */
    ByteArrayResource exportExcelUsers(UserFilterDTO filter) throws IOException;
}
