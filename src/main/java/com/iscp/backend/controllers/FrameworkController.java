package com.iscp.backend.controllers;

import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.FrameworkCategoryNotFoundException;
import com.iscp.backend.exceptions.FrameworkNotFoundException;
import com.iscp.backend.services.FrameworkService;
import com.iscp.backend.services.impl.FrameworkServiceImpl;
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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Controller for managing framework related operations.
 */
@RestController
@RequestMapping(path = FrameworkController.PATH,produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
public class FrameworkController {

    public final static String PATH = "/api/framework";

    private final FrameworkService frameworkService;

    /**
     * Retrieves a list of all frameworks.
     *
     * @return a ResponseEntity containing a list of frameworks.
     */
    @Operation(summary = "To get all frameworks", description = "To get all existing frameworks in the database")
    @GetMapping("/getAll-frameworks")
    public ResponseEntity<List<FrameworkDTO>> getAllFrameworks() {
        log.info("Received request to get all frameworks");

        //call getAllFrameworks to fetch frameworks list.
        List<FrameworkDTO> frameworks = frameworkService.getAllFrameworks();

        return ResponseEntity.status(HttpStatus.OK).body(frameworks);
    }


    /**
     * Add or update a list of framework in the database.
     *
     * @param frameworkCreateDTOList a list of FrameworkCreateDTO containing details of framework to be added or updated.
     * @return a ResponseEntity containing a list of FrameworkDTO.
     * @throws FrameworkCategoryNotFoundException if the specifies framework category does not exist.
     * @throws FrameworkNotFoundException if a framework ID is not found in the repository.
     */
    @Operation(summary = "Add and edit framework", description = "Creates a new framework entry in the database and if present then update it")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "Framework created",content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = FrameworkDTO.class))
            }),
            @ApiResponse(responseCode = "409",description = "Framework Category Not Found",content = @Content)
    })
    @PostMapping("/add-edit-framework")
    public ResponseEntity<List<FrameworkDTO>> addEditFramework( @RequestBody List<FrameworkCreateDTO> frameworkCreateDTOList) throws FrameworkCategoryNotFoundException, FrameworkNotFoundException {
        log.info("Received request to Add or Update a list of framework");

        //call addEditFramework to add or update a framework.
        List<FrameworkDTO> frameworkDTOList = frameworkService.addEditFramework(frameworkCreateDTOList);

        return ResponseEntity.status(HttpStatus.CREATED).body(frameworkDTOList);
    }


    /**
     * Fetch list of framework associated to a framework category.
     *
     * @param frameworkCategoryName the name of framework category to fetch frameworks from.
     * @return a ResponseEntity containing a list of FrameworkDTO.
     * @throws FrameworkCategoryNotFoundException if the specified framework category does not exist.
     */
    @Operation(summary = "Get all framework from framework category", description = "Fetch all framework for a specific framework category from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Frameworks fetched successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ControlDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Framework Category not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/frameworks-from-framework-category/{frameworkCategoryName}")
    public ResponseEntity<List<FrameworkDTO>> getFrameworkFromFrameworkCategory(@PathVariable  String frameworkCategoryName) throws FrameworkCategoryNotFoundException {
        log.info("Received request to get all framework for framework-category :{}",frameworkCategoryName);

        //call getFrameworkFromFrameworkCategory to fetch list of frameworks associated to a framework category.
        List<FrameworkDTO> frameworks=frameworkService.getFrameworkFromFrameworkCategory(frameworkCategoryName);

        log.info("All Frameworks fetched successfully:{}",frameworks.size());
        return  ResponseEntity.status(HttpStatus.OK).body(frameworks);
    }


    /**
     * Exports framework data to an Excel.
     *
     * @param filter FrameworkFilterDTO containing the filtering criteria to be encapsulated.
     * @return a ResponseEntity containing a ByteArrayResource representing the Excel.
     * @throws IOException if an error occurs during export.
     */
    @Operation(summary = "To export data to excel sheet", description = "To export table data to excel sheet of framework")
    @PostMapping("/export-excel")
    public ResponseEntity<ByteArrayResource> exportsExcelFramework(@RequestBody FrameworkFilterDTO filter) throws IOException {
        log.info("Received request to export framework table data to excel sheet");

        //Export the fetched data to an Excel
        ByteArrayResource resource = frameworkService.exportExcelFramework(filter);
        log.info("Exported framework data to Excel successfully");

        String fileName = "framework.xlsx";

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(resource.contentLength())
                .body(resource);
    }


    /**
     * Retrieves a paginated list of filtered framework records based on provided filters.
     *
     * @param frameworkFilterDTO FrameworkFilterDTO containing the filtering criteria to be encapsulated.
     * @return a PaginatedResponse containing FrameworkDTO objects.
     */
    @Operation(summary = "Get paginated Framework using filters", description = "Fetch  Framework using filters from the database as per pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated Framework fetched successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = FrameworkDTO.class)))),
            @ApiResponse(responseCode = "204", description = "No Framework found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/get-filtered-framework-paginated")
    public ResponseEntity<PaginatedResponse<FrameworkDTO>> getAllFrameworkFiltered(@RequestBody FrameworkFilterDTO frameworkFilterDTO) {
        log.info("Received request to get all Framework ");

        //call getFilterFrameworkPaginated method to retrieve paginated framework records based on filters.
        PaginatedResponse<FrameworkDTO> filterFrameworkPaginated = frameworkService.getFilterFrameworkPaginated(frameworkFilterDTO);

        log.info("All Framework fetched successfully:{}",filterFrameworkPaginated.getTotalElements());
        return  ResponseEntity.status(HttpStatus.OK).body(filterFrameworkPaginated);
    }
}
