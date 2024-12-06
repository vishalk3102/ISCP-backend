package com.iscp.backend.controllers;

import com.iscp.backend.dto.DepartmentDTO;
import com.iscp.backend.services.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for managing department related operations.
 */
@RestController
@RequestMapping(path = DepartmentController.PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
public class DepartmentController {

    private final DepartmentService departmentService;

    public final static String PATH = "/api/department";

    /**
     * Retrieves a list of all departments.
     *
     * @return a ResponseEntity containing a list of DepartmentDTO.
     */
    @Operation(summary = "Get all Department", description = "Get list of all Departments from the database")
    @ApiResponses(value = {@ApiResponse(responseCode = "201",description = "list of department",content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "204", description = "No Department found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/get-all-department")
    public ResponseEntity<List<DepartmentDTO>> getAllDepartment() {
        log.info("Fetch list of all Department");

        //call getALlDepartment to fetch department list.
        List<DepartmentDTO> getAllDepartments = departmentService.getAllDepartmentNames();

        log.info("Departments fetched successfully");
        return ResponseEntity.status(HttpStatus.OK).body(getAllDepartments);
    }


    /**
     * Retrieves a list of all departments associated to the currently logged-in user.
     *
     * @return a ResponseEntity containing a list of DepartmentDTO.
     */
    @Operation(summary = "Get all Department of Logged in User", description = "Get list of all Departments associated to logged-in user from the database")
    @ApiResponses(value = {@ApiResponse(responseCode = "201",description = "list of department",content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "204", description = "No Department found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/get-login-user-department")
    public ResponseEntity<List<DepartmentDTO>> getLogInUserDepartments() {
        log.info("Fetch list of Logged in User Departments");

        //call getLogInUserDepartments to fetch departments related to loggedIn user
        List<DepartmentDTO> getAllDepartments = departmentService.getLogInUserDepartments();

        log.info("Departments  fetched successfully");
        return ResponseEntity.status(HttpStatus.OK).body(getAllDepartments);
    }
}
