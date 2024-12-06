package com.iscp.backend.controllers;

import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.*;
import com.iscp.backend.services.SecurityComplianceService;
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
 * Controller for managing security compliance-related operations.
 */
@RestController
@RequestMapping(path = SecurityComplianceController.PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
public class SecurityComplianceController {

    public final static String PATH = "/api/security-compliance";

    private final SecurityComplianceService securityComplianceService;

    /**
     * Add or update a list of security compliance in the database.
     *
     * @param securityComplianceCreateDTOList a list of SecurityComplianceCreateDTO containing details of security compliance to be added or updated.
     * @return a ResponseEntity containing a list of SecurityComplianceDTOs.
     * @throws ControlNotFoundException if the specified control does not exist.
     * @throws ChecklistNotFoundException if any of the specified checklist does not exist.
     * @throws FrameworkNotFoundException if the specified framework does not exist.
     * @throws DepartmentNotFoundException if any of the specified department does not exist.
     * @throws SecurityComplianceNotFoundException if a security ID is not found in the repository.
     * @throws PeriodicityUpdateDeniedException if the periodicity update is denied
     */
    @Operation(summary = "To add and Update Security Compliance", description = "To add and Update security compliance in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Security Compliance Added", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(type = "array", implementation = SecurityComplianceDTO.class))}),
            @ApiResponse(responseCode = "400",description = "Security Compliance Not Found",content = @Content),
            @ApiResponse(responseCode = "404",description = "Control Not Found",content = @Content),
            @ApiResponse(responseCode = "407",description = "Checklist Not Found",content = @Content),
            @ApiResponse(responseCode = "408", description = "Framework Not Found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Department Not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/add-edit-security-compliance")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<SecurityComplianceDTO>> addSecurityCompliance(@RequestBody List<SecurityComplianceCreateDTO> securityComplianceCreateDTOList) throws ControlNotFoundException, ChecklistNotFoundException, FrameworkNotFoundException, DepartmentNotFoundException, SecurityComplianceNotFoundException, PeriodicityUpdateDeniedException {
        log.info("Received request to Add or Update Security Compliance");

        //call addEditSecurityCompliance to add or edit security compliance
        List<SecurityComplianceDTO> securityComplianceDTOList = securityComplianceService.addEditSecurityCompliance(securityComplianceCreateDTOList);

        log.info("Security Compliance added or updated successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(securityComplianceDTOList);
    }


    /**
     * Retrieves a paginated list of filtered security compliance records based on provided filters.
     *
     * @param securityComplianceFilterDTO SecurityComplianceFilterDTO containing the filtering criteria to be encapsulated.
     * @return a PaginatedResponse containing SecurityComplianceDTO objects.
     */
    @Operation(summary = "Get paginated Security Compliance using filters", description = "Fetch Security Compliance using filters from the database as per pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated Security Compliance fetched successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = SecurityComplianceDTO.class)))),
            @ApiResponse(responseCode = "204", description = "No Security Compliance found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/get-filtered-security-compliance-paginated")
    public ResponseEntity<PaginatedResponse<SecurityComplianceDTO>> getAllSecurityComplianceFiltered(@RequestBody SecurityComplianceFilterDTO securityComplianceFilterDTO) {
        log.info("Received request to get all Security Compliance {}", securityComplianceFilterDTO);

        //call getFilterSecurityCompliancePaginated method to retrieve paginated security compliance records based on filters
        PaginatedResponse<SecurityComplianceDTO> securityComplianceDTOPaginatedResponse = securityComplianceService.getFilterSecurityCompliancePaginated(securityComplianceFilterDTO);

        log.info("All Security Compliance  fetched successfully:{}", securityComplianceDTOPaginatedResponse.getTotalElements());
        return  ResponseEntity.status(HttpStatus.OK).body(securityComplianceDTOPaginatedResponse);
    }


    /**
     * Exports security compliance to an Excel.
     *
     * @param filter SecurityComplianceFilterDTO containing the filtering criteria to be encapsulated.
     * @return a ResponseEntity containing a ByteArrayResource representing the Excel.
     * @throws IOException if an error occurs during export.
     */
    @Operation(summary = "To export data to excel sheet", description = "To export table data to excel sheet of Security Compliance")
    @PostMapping("/export-excel")
    public ResponseEntity<ByteArrayResource> exportsExcelSecurityCompliance(@RequestBody SecurityComplianceFilterDTO filter) throws IOException {
        log.info("Received request to export security compliance table data to excel sheet");

        //Export the fetched data to an Excel
        ByteArrayResource resource = securityComplianceService.exportExcelSecurityCompliance(filter);
        log.info("Exported security compliance data to Excel successfully");

        String fileName = "security-compliance.xlsx";

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(resource.contentLength())
                .body(resource);

    }


    /**
     * Retrieves a list of SecurityComplianceDTO for the given list of complianceId.
     *
     * @param complianceIds a list of complianceId whose associated security compliance need to retrieve.
     * @return a ResponseEntity containing a list of lists of SecurityComplianceDTOs.
     * @throws SecurityComplianceNotFoundException if a security ID is not found in the repository.
     */
    @Operation(summary = "Get all the Security Compliance on the basis of Compliance Id", description = "Get list of list of List of Security Compliance on the basis of Compliance Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Security Compliance fetched successfully", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = String.class))),
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @GetMapping("/security-compliance")
    public ResponseEntity<List<List<SecurityComplianceDTO>>> getSecurityCompliance(@RequestParam List<String> complianceIds) throws SecurityComplianceNotFoundException {
        log.info("Received request to get all security compliance on the basis of compliance Id");

        //call getSecurityCompliance method to fetch the security compliance
        List<List<SecurityComplianceDTO>> securityComplianceList = securityComplianceService.getSecurityCompliance(complianceIds);

        return ResponseEntity.status(HttpStatus.OK).body(securityComplianceList);
    }


    /**
     * Retrieves a list of all security compliance
     *
     * @return a ResponseEntity containing a list of SecurityComplianceDTOs.
     */
    @Operation(summary = "Get All security ComplianceDTO", description = "Get List of All security ComplianceDTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Security Compliance fetched successfully", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = String.class))),
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @GetMapping("/get-all-security-compliance")
    public ResponseEntity<List<SecurityComplianceDTO>> getAllSecurityCompliance()  {
        log.info("Received request to get all security compliance");

        //Call getAllSecurityComplianceDTOs to fetch securityComplianceDTO
        List<SecurityComplianceDTO> securityComplianceList = securityComplianceService.getAllSecurityComplianceDTOs();

        return ResponseEntity.status(HttpStatus.OK).body(securityComplianceList);
    }


    /**
     * Checks whether a particular compliance exists or not.
     *
     * @param securityComplianceCheckDTO the DTO containing details for checking compliance status.
     * @return a ResponseEntity containing a Boolean to determine the existence of the compliance.
     */
    @Operation(summary = "To check the compliance status", description = "to check whether compliance exists or not")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Compliance Status fetched successfully", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SecurityComplianceExistsDTO.class)),
            }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PostMapping("/check-compliance-exists")
    public ResponseEntity<Boolean> isComplianceExists(@RequestBody SecurityComplianceCheckDTO securityComplianceCheckDTO) {
        log.info("Received request to check whether particular compliance exists or not");

        //call isSecurityComplianceAlreadyExists to determine the existence of a compliance
        Boolean result = securityComplianceService.isSecurityComplianceAlreadyExists(securityComplianceCheckDTO);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}