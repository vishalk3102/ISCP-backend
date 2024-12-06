package com.iscp.backend.controllers;

import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.*;
import com.iscp.backend.services.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Controller for managing role related operations.
 */
@RestController
@Slf4j
@RequestMapping(path = RoleController.PATH,produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class RoleController {

    public final static String PATH = "/api/role";

    private final RoleService roleService;

    /**
     * Retrieves a list of all roles.
     *
     * @return a ResponseEntity containing a list of RoleDTO.
     */
    @Operation(summary = "Get all roles", description = "Fetch all roles from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles fetched successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = RoleDTO.class)))),
            @ApiResponse(responseCode = "204", description = "No roles found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/get-all-roles")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        log.info("Received request to get all roles");

        //call getAllRoles to fetch roles list.
        List<RoleDTO> roles = roleService.getAllRoles();

        log.info("All Roles fetched successfully:{}", roles.size());
        return ResponseEntity.status(HttpStatus.OK).body(roles);
    }


    /**
     * Add or update a list of role in the database.
     *
     * @param createRoleDTOList a list of RoleCreateDTO containing details of role to be added or updated.
     * @return a ResponseEntity containing a list of RoleDTO.
     * @throws RoleNotFoundException if a role ID is not found in the repository.
     * @throws RoleAlreadyExistException if a role already exists.
     */
    @Operation(summary = "Add a new role or update an existing role", description = "Add a new role or Update an existing role entry in the database")
    @ApiResponses(value = {@ApiResponse(responseCode = "201",description = "Role updated",content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = RoleDTO.class))}),
            @ApiResponse(responseCode = "400",description = "Role Not Found",content = @Content),
            @ApiResponse(responseCode = "408", description = "Role Already Exists", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/add-edit-role")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<RoleDTO>> addEditRole(@RequestBody List<RoleCreateDTO> createRoleDTOList) throws RoleNotFoundException, RoleAlreadyExistException {
        log.info("Received request to Add or Update role");

        //call addUpdateRole to add or update role
        List<RoleDTO> roleDTOList = roleService.addUpdateRole(createRoleDTOList);

        log.info("roles added or updated successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(roleDTOList);
    }


    //SEARCH FILTER
    @Operation(summary = "Get paginated role using filters", description = "Fetch role using filters from the database as per pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated Roles fetched successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = RoleDTO.class)))),
            @ApiResponse(responseCode = "204", description = "No Role found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/get-filtered-role-paginated")
    public ResponseEntity<PaginatedResponse<RoleDTO>> getAllRoleFiltered(@RequestBody RoleFilterDTO filter)
    {
        log.info("Received request to get all Role ");

        //call getAllRolesPaginatedSearchFilter method in service
        PaginatedResponse<RoleDTO> roleDTOPaginatedResponse = roleService.getAllRolesPaginatedSearchFilter(filter);

        log.info("All Role fetched successfully:{}",roleDTOPaginatedResponse.getTotalElements());
        return ResponseEntity.status(HttpStatus.OK).body(roleDTOPaginatedResponse);
    }


    @Operation(summary = "To export data to excel sheet", description = "To export table data to excel sheet of role")
    @PostMapping("/export-excel")
    public ResponseEntity<ByteArrayResource> exportsExcelRole(@RequestBody RoleFilterDTO filter) throws IOException {
        log.info("Received request to export framework table data to excel sheet");

        ByteArrayResource resource = roleService.exportExcelRole(filter);
        log.info("Exported role data to Excel successfully");

        String fileName = "role.xlsx";

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(resource.contentLength())
                .body(resource);

    }
}
