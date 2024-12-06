package com.iscp.backend.mapper;

import com.iscp.backend.dto.PermissionDTO;
import com.iscp.backend.models.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for mapping between Permission Entities and PermissionDTO.
 */
@Mapper(componentModel = "spring")
public interface PermissionMapper {

    /**
     * Mapping PermissionDTO to Permission Entity.
     *
     * @param permissionDTO the permissionDTO to convert.
     * @return the converted Permission Entity.
     */
    Permission toEntity(PermissionDTO permissionDTO);


    /**
     * Mapping Permission Entity to PermissionDTO.
     *
     * @param permission the Permission Entity to convert.
     * @return the converted PermissionDTO.
     */
    PermissionDTO toDto(Permission permission);
}
