package com.iscp.backend.mapper;

import com.iscp.backend.dto.RoleCreateDTO;
import com.iscp.backend.dto.RoleDTO;
import com.iscp.backend.dto.RoleUpdateDTO;
import com.iscp.backend.models.Enum;
import com.iscp.backend.models.Permission;
import com.iscp.backend.models.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Mapper interface for mapping between Role entities and RoleDTO.
 */
@Mapper(componentModel = "spring")
public interface RoleMapper {

    /**
     * Mapping Role Entity to RoleDTO.
     *
     * @param role the Role Entity to convert.
     * @return the converted RoleDTO.
     */
    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "permissionsToPermissionName")
    RoleDTO toRoleDTO(Role role);


    /**
     * Mapping RoleCreateDTO to Role Entity.
     *
     * @param roleCreateDTO the RoleCreateDTO to convert.
     * @return the converted Role Entity.
     */
    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "permissionTypeToPermissions")
    @Mapping(target = "roleId", ignore = true)
    @Mapping(target = "users", ignore = true)
    Role toRoleEntity(RoleCreateDTO roleCreateDTO);


    /**
     * Mapping RoleUpdateDTO to Role Entity.
     *
     * @param roleUpdateDTO the RoleCreateDTO to convert.
     * @param role the converted Role Entity.
     */
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "roleName", ignore = true)
    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "permissionTypeToPermissions")
    void updateRoleEntityFromDTO(RoleUpdateDTO roleUpdateDTO, @MappingTarget Role role);


    /**
     * Mapping list of Role Entity to list of RoleDTO.
     *
     * @param role the list of Role Entity to convert.
     * @return the list of converted RoleDTO.
     */
    List<RoleDTO> toRoleDTOs(List<Role> role);

    //Custom mapping for Permission to String (PermissionName)
    @Named("permissionsToPermissionName")
    default Set<String> mapPermissionsToName(Set<Permission> permissions) {
        if (permissions == null) {
            return null;
        }
        return permissions.stream()
                .map(permission -> permission.getRolePermissions().name())
                .collect(Collectors.toSet());
    }

    //Custom mapping for PermissionType to String (Permissions)
    @Named("permissionTypeToPermissions")
    default Set<Permission> mapPermissionTypeToPermissions(Set<Enum.PermissionType> permissionTypes) {
        if (permissionTypes == null) {
            return null;
        }
        return permissionTypes.stream()
                .map(permissionType -> {
                    Permission permission = new Permission();
                    permission.setRolePermissions(permissionType);
                    return permission;
                })
                .collect(Collectors.toSet());
    }


    /**
     * Mapping RoleCreateDTO to RoleUpdateDTO.
     *
     * @param roleDTO the RoleCreateDTO to convert.
     * @return the converted RoleUpdateDTO.
     */
    RoleUpdateDTO toUpdateRoleDto(RoleCreateDTO roleDTO);
}
