package com.iscp.backend.controllers;

import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.FrameworkCategoryExistsException;
import com.iscp.backend.exceptions.FrameworkCategoryNotFoundException;
import com.iscp.backend.services.impl.FrameworkCategoryServiceImpl;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing framework category related operations.
 */
@Slf4j
@AllArgsConstructor
@RequestMapping(path = FrameworkCategoryController.PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class FrameworkCategoryController {

    public final static String PATH = "/api/framework-category";

    private final FrameworkCategoryServiceImpl frameworkCategoryService;

    /**
     * Retrieves a list of all framework categories.
     *
     * @return a ResponseEntity containing a list of FrameworkCategoryDTO.
     */
    @Operation(summary = "To get all frameworks categories", description = "To get all existing frameworks categories in the database")
    @GetMapping("/getAll-frameworks-categories")
    public ResponseEntity<List<FrameworkCategoryDTO>> getAllFrameworkCategories() {
        log.info("Received request to get all frameworks categories");

        //call getAllFrameworkCategories to fetch framework category list.
        List<FrameworkCategoryDTO> frameworkCategories= frameworkCategoryService.getAllFrameworkCategories();

        return ResponseEntity.status(HttpStatus.OK).body(frameworkCategories);
    }


    /**
     * Add a new framework Category in the database.
     *
     * @param frameworkCategoryCreateDTO a FrameworkCategoryCreateDTO containing details of framework category to be added.
     * @return a ResponseEntity containing added FrameworkCategoryDTO.
     * @throws FrameworkCategoryExistsException if a framework category already exists.
     */
    @Operation(summary = "Add a new framework category", description = "Creates a new framework category entry in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "Framework Category created",content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = FrameworkCategoryDTO.class))
            }),
            @ApiResponse(responseCode = "400",description = "Framework Category not Added",content = @Content)
    })
    @PreAuthorize("hasRole('Admin')")
    @PostMapping("/add-framework-category")
    public ResponseEntity<FrameworkCategoryDTO> addFrameworkCategory(@RequestBody FrameworkCategoryCreateDTO frameworkCategoryCreateDTO) throws FrameworkCategoryExistsException {
        log.info("Received request to add framework category: {}", frameworkCategoryCreateDTO);

        //call addFrameworkCategory to add a new framework category.
        FrameworkCategoryDTO addedFrameworkCategory = frameworkCategoryService.addFrameworkCategory(frameworkCategoryCreateDTO);

        log.info("Framework Category added successfully: {}", addedFrameworkCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedFrameworkCategory);
    }


    /**
     * Update an existing framework Category in the database.
     *
     * @param frameworkCategoryUpdateDTOS a FrameworkCategoryUpdateDTO containing details of framework category to be updated.
     * @return a ResponseEntity containing updated FrameworkCategoryDTO.
     * @throws FrameworkCategoryNotFoundException if a framework category ID is not found in the repository.
     * @throws FrameworkCategoryExistsException if a framework category already exists.
     */
    @Operation(summary = "Update a existing framework category", description = "Update a existing framework category in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Framework Category Updated",content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = FrameworkCategoryDTO.class))
            }),
            @ApiResponse(responseCode = "409",description = "Framework category Not Found",content = @Content)
    })
    @PreAuthorize("hasRole('Admin')")
    @PutMapping("/update-framework-category")
    public ResponseEntity<List<FrameworkCategoryDTO>> updateFrameworkCategory(@RequestBody List<FrameworkCategoryUpdateDTO> frameworkCategoryUpdateDTOS) throws FrameworkCategoryNotFoundException, FrameworkCategoryExistsException {
        log.info("Received request to update framework category : {}",frameworkCategoryUpdateDTOS);

        //call updateFrameworkCategory to update an existing framework category.
        List<FrameworkCategoryDTO> updatedFrameworkCategory = frameworkCategoryService.updateFrameworkCategory(frameworkCategoryUpdateDTOS);

        return ResponseEntity.status(HttpStatus.OK).body(updatedFrameworkCategory);
    }
}
