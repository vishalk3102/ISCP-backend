package com.iscp.backend.services.impl;

import com.iscp.backend.components.ExportExcel;
import com.iscp.backend.components.Pagination;
import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.*;
import com.iscp.backend.mapper.RoleMapper;
import com.iscp.backend.models.Enum;
import com.iscp.backend.models.Permission;
import com.iscp.backend.models.Role;
import com.iscp.backend.repositories.PermissionRepository;
import com.iscp.backend.repositories.RoleRepository;
import com.iscp.backend.services.RoleService;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link RoleService} interface for managing roles.
 */
@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private final PermissionRepository permissionRepository;

    private final RoleMapper roleMapper;

    private final ExportExcel exportExcel;


    /**
     * Retrieves a list of all roles from the database.
     *
     * @return a list of {@link RoleDTO} containing all roles, or an empty list if no roles are found.
     */
    @Override
    public List<RoleDTO> getAllRoles() {
        log.debug("Attempting to get all Roles");

        //Retrieve and sort the list of all roles by their name in ascending order
        List<Role> roles=roleRepository.findAll(Sort.by(Sort.Direction.ASC,"roleName"));

        //Check if the retrieved list is empty
        if (roles.isEmpty()) {
            log.info("No roles found");
            return List.of();
        }
        log.info("Fetched roles :{}", roles);

        //Convert list of Role Entity to list of Role DTO and return
        return roleMapper.toRoleDTOs(roles);
    }


    /**
     * Add a new role based on the provided role DTO.
     *
     * @param roleCreateDTO DTO containing role details to be added.
     * @return {@link RoleDTO} containing added new role.
     * @throws RoleAlreadyExistException if the provided role already exists.
     */
    @Override
    public RoleDTO addRole(RoleCreateDTO roleCreateDTO) throws RoleAlreadyExistException {
        log.debug("Adding new role: {}", roleCreateDTO);

        // Check If a role with the same name already exists
        Optional<Role> existingRole = roleRepository.findByRoleName(roleCreateDTO.getRoleName());
        if(existingRole.isPresent()) {
            log.warn("Role with name {}  already exists", roleCreateDTO.getRoleName());
            throw new RoleAlreadyExistException();
        }

        //Convert CreateRoleDTO to Role Entity
        Role role = roleMapper.toRoleEntity(roleCreateDTO);
        //Save the Role to the database
        Role savedRole = roleRepository.save(role);

        log.info("Role added successfully with id: {}", savedRole.getRoleId());
        // Convert saved role entity to DTO and return
        return roleMapper.toRoleDTO(savedRole);
    }


    /**
     * Update a list of roles based on the provided list of role DTO.
     *
     * @param roleUpdateDTOS a list of RoleUpdateDTO containing role details to be updated.
     * @return a list of {@link RoleDTO} containing updated roles.
     */
    @Override
    public List<RoleDTO> updateExistingRoles(List<RoleUpdateDTO> roleUpdateDTOS) {
        log.debug("Attempting to update Roles");

        //Fetch all existing permissions once and map them by their role types
        Map<Enum.PermissionType,Permission> permissionMap = permissionRepository.findAll()
                .stream()
                .filter(permission -> permission.getRolePermissions()!=null)
                .collect(Collectors.toMap(Permission::getRolePermissions,permission -> permission,(p1,p2)->p1));

        List<Role> updatedRoles = roleUpdateDTOS.stream().map(roleUpdateDTO -> {
            try {
                //Update single role based on roleUpdateDTO and permission map
                return updateSingleRole(roleUpdateDTO,permissionMap);
            }
            catch (RoleNotFoundException | PermissionNotFoundException e) {
                log.error("Role not found with  id {}",roleUpdateDTO.getRoleId());
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        //Save all updated roles
        List<Role> savedRoles = roleRepository.saveAll(updatedRoles);
        log.info("Updated {} Roles successfully ", savedRoles.size());

        //Convert the updated roles to DTO and return
        return savedRoles.stream().map(roleMapper::toRoleDTO).collect(Collectors.toList());
    }


    /**
     * Update a single role based on the provided role DTO and permission map.
     *
     * @param roleUpdateDTO DTO containing role details to be updated.
     * @param permissionMap a map of permissions indexed by their types.
     * @return the updated {@link Role} object.
     * @throws RoleNotFoundException if the specified role does not exist
     * @throws PermissionNotFoundException if the specified permission does not exist
     */
    @Override
    public Role updateSingleRole(RoleUpdateDTO roleUpdateDTO, Map<Enum.PermissionType, Permission> permissionMap) throws RoleNotFoundException, PermissionNotFoundException {
        log.debug("Attempting to update Role with id: {}", roleUpdateDTO.getRoleId());

        //Find the existing Role by ID
        Optional<Role> roleOptional=roleRepository.findById(roleUpdateDTO.getRoleId());
        if(roleOptional.isEmpty()) {
            log.error("Role not found with id {}", roleUpdateDTO.getRoleId());
            throw new RoleNotFoundException();
        }
        Role role=roleOptional.get();

        //Check if permissions are provided in the update DTO
        if(roleUpdateDTO.getPermissions() != null && !roleUpdateDTO.getPermissions().isEmpty()) {
            //Fetch all permissions from the database that match the names in roleUpdateDTO
            Set<Permission> updatedPermissions = roleUpdateDTO.getPermissions().stream()
                    .map(permissionType -> {
                        Permission permission=permissionMap.get(permissionType);
                        if(permission == null) {
                            log.error("Permission {} not found",permissionType);
                            try {
                                throw  new PermissionNotFoundException();
                            }
                            catch (PermissionNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        return permission;
                    }).collect(Collectors.toSet());

            //Set the new permissions to the role's permissions set
            role.setPermissions(updatedPermissions);
        }
        //Update the status of the role with the new status from the DTO
        role.setStatus(roleUpdateDTO.getStatus());
        return role;
    }


    /**
     * Add or update a list of roles based on the provided list of role DTO.
     *
     * @param createRoleDTOList a list of RoleCreateDTO containing role details to be added or updated.
     * @return a list of {@link RoleDTO} containing added or updated roles.
     * @throws RoleNotFoundException if the specified role does not exist
     * @throws RoleAlreadyExistException if the provided role already exists.
     */
    public List<RoleDTO> addUpdateRole(List<RoleCreateDTO> createRoleDTOList) throws  RoleNotFoundException, RoleAlreadyExistException {
        //List of successfully added/updated roles
        List<RoleDTO> allRolesList = new ArrayList<>();

        //List to store roles which needs to be updated
        List<RoleUpdateDTO> updateRoleList = new ArrayList<>();

        for(RoleCreateDTO roleDTO : createRoleDTOList) {
            String id = roleDTO.getRoleId();

            //If roleId is blank, then call addRole method to add the role
            if(id.isBlank()) {
                allRolesList.add(addRole(roleDTO));
            }
            //If roleId is not found, then throw an exception
            else if(roleRepository.findById(roleDTO.getRoleId()).isEmpty()) {
                throw new RoleNotFoundException();
            }
            //add the role to updateRoleList
            else {
                RoleUpdateDTO updateRoleDTO = roleMapper.toUpdateRoleDto(roleDTO);
                updateRoleList.add(updateRoleDTO);
            }
        }
        //Update existing roles in the repository
        allRolesList.addAll(updateExistingRoles(updateRoleList));
        return allRolesList;
    }


    /**
     * Retrieves a paginated list of filtered role records.
     *
     * @param filter RoleFilterDTO containing the filtering criteria to be encapsulated.
     * @return a PaginatedResponse containing RoleDTO objects.
     */
    @Override
    public PaginatedResponse<RoleDTO> getAllRolesPaginatedSearchFilter(RoleFilterDTO filter) {
        log.debug("Attempting to get roles on the basis of filter");

        //Specification for filtering role based on provided criteria
        Specification<Role> spec = roleFilter(filter.getRoleName(), filter.getStatus());

        //Pageable object for pagination and sorting
        Pageable pageable= Pagination.createPageable(filter.getPage(),filter.getSize(),filter.getSortField(),filter.getSortOrder());

        //Retrieve paginated list of roles
        Page<Role> roleList = roleRepository.findAll(spec,pageable);

        //Map Role to RoleDTO
        Page<RoleDTO> RoleDTOPage= roleList.map(roleMapper::toRoleDTO);

        return Pagination.createdPaginatedContent(RoleDTOPage);
    }


    /**
     * Create a Specification for filtering roles based on various criteria.
     *
     * @param role the list of RoleName to filter by.
     * @param status the role status to filter by.
     * @return the specification for filtering role entity.
     */
    private Specification<Role> roleFilter(String role, Boolean status) {
        return (root, query, criteriaBuilder) ->
        {
            List<Predicate> predicates = new ArrayList<>();

            // Check if the role filter is provided
            if(role !=null) {
                predicates.add(criteriaBuilder.equal(root.get("roleName"),role));
                log.info("Added predicate for role: {}",role);
            }

            // Check if the status filter is provided
            if(status!=null) {
                predicates.add(criteriaBuilder.equal(root.get("status"),status));
                log.info("Added predicate for status: {}", status);
            }

            //Combine all predicates into a single Predicate for the query
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


    /**
     * Exports role records to an Excel.
     *
     * @param filter RoleFilterDTO containing the filtering criteria to be encapsulated.
     * @return a ByteArrayResource containing the Excel file data.
     * @throws IOException if an error occurs during export.
     */
    @Override
    public ByteArrayResource exportExcelRole(RoleFilterDTO filter) throws IOException {
        log.info("Starting export of role data to Excel");

        // Retrieve all role DTOs
        PaginatedResponse<RoleDTO> paginatedResponse = getAllRolesPaginatedSearchFilter(filter);
        List<RoleDTO> roleList = paginatedResponse.getContent();
        log.debug("Fetched {} role records for export", roleList.size());

        // Output stream and configuration for Excel export
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        List<String> fieldsToInclude = List.of(
                "roleName",
                "permissions",
                "status"
        );
        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("roleName", "ROLE NAME");
        customHeaders.put("permissions", "PERMISSIONS");
        customHeaders.put("status", "STATUS");

        // Use the existing export method
        exportExcel.exportToExcel(outputStream, roleList, "roles", fieldsToInclude, customHeaders);
        log.info("Successfully exported role data to Excel");

        // Convert Output Stream to ByteArrayResource
        byte[] excelContent = outputStream.toByteArray();
        ByteArrayResource resource = new ByteArrayResource(excelContent);
        return resource;
    }
}