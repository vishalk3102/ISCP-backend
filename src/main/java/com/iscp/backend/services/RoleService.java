package com.iscp.backend.services;

import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.PermissionNotFoundException;
import com.iscp.backend.exceptions.RoleAlreadyExistException;
import com.iscp.backend.exceptions.RoleNotFoundException;
import com.iscp.backend.models.Enum;
import com.iscp.backend.models.Permission;
import com.iscp.backend.models.Role;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Service Interface for managing role.
 */
public interface RoleService {

    /**
     * Retrieves a list of all roles from the database.
     *
     * @return a list of {@link RoleDTO} containing all roles, or an empty list if no roles are found.
     */
    List<RoleDTO> getAllRoles();


    /**
     * Add a new role based on the provided role DTO.
     *
     * @param roleCreateDTO DTO containing role details to be added.
     * @return {@link RoleDTO} containing added new role.
     * @throws RoleAlreadyExistException if the provided role already exists.
     */
    RoleDTO addRole(RoleCreateDTO roleCreateDTO) throws RoleAlreadyExistException;


    /**
     * Update a list of roles based on the provided list of role DTO.
     *
     * @param roleUpdateDTOS a list of RoleUpdateDTO containing role details to be updated.
     * @return a list of {@link RoleDTO} containing updated roles.
     */
    List<RoleDTO> updateExistingRoles(List<RoleUpdateDTO> roleUpdateDTOS);


    /**
     * Update a single role based on the provided role DTO and permission map.
     *
     * @param roleUpdateDTO DTO containing role details to be updated.
     * @param permissionMap a map of permissions indexed by their types.
     * @return the updated {@link Role} object.
     * @throws RoleNotFoundException if the specified role does not exist
     * @throws PermissionNotFoundException if the specified permission does not exist
     */
    Role updateSingleRole(RoleUpdateDTO roleUpdateDTO, Map<Enum.PermissionType, Permission> permissionMap) throws RoleNotFoundException, PermissionNotFoundException;


    /**
     * Add or update a list of roles based on the provided list of role DTO.
     *
     * @param createRoleDTOList a list of RoleCreateDTO containing role details to be added or updated.
     * @return a list of {@link RoleDTO} containing added or updated roles.
     * @throws RoleNotFoundException if the specified role does not exist
     * @throws RoleAlreadyExistException if the provided role already exists.
     */
    List<RoleDTO> addUpdateRole(List<RoleCreateDTO> createRoleDTOList) throws RoleAlreadyExistException, RoleNotFoundException;


    /**
     * Retrieves a paginated list of filtered role records.
     *
     * @param filter RoleFilterDTO containing the filtering criteria to be encapsulated.
     * @return a PaginatedResponse containing RoleDTO objects.
     */
    PaginatedResponse<RoleDTO> getAllRolesPaginatedSearchFilter(RoleFilterDTO filter);


    /**
     * Exports role records to an Excel.
     *
     * @param filter RoleFilterDTO containing the filtering criteria to be encapsulated.
     * @return a ByteArrayResource containing the Excel file data.
     * @throws IOException if an error occurs during export.
     */
    ByteArrayResource exportExcelRole(RoleFilterDTO filter) throws IOException;
}
