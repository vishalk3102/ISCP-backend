package com.iscp.backend.controllers;


import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.ControlCategoryAlreadyExistsException;
import com.iscp.backend.exceptions.ControlCategoryNotFoundException;
import com.iscp.backend.services.ControlCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = ControlCategoryController.PATH,produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
public class ControlCategoryController {
    public static final String PATH ="/api/control-category" ;

    private final ControlCategoryService controlCategoryService;


    //GET ALL CONTROL CATEGORIES
    @Operation(summary = "Get all control categories", description = "Fetch all control categories from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Control categories fetched successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ControlCategoryDTO.class)),
                            examples = @ExampleObject(value = """
                                    [
                                        {
                                            "controlCategoryId": "7de4eb1c-56e4-40cd-a5c6-d8d7d8e9f697",
                                            "controlCategoryName": "ISMS 1",
                                            "controlList": [
                                              {
                                                "controlId": "0780a335-7e8d-43eb-b6de-1ffc9ab3c424",
                                                "controlName": "Access Control2",
                                                "description": "string",
                                                "status": true,
                                                "controlCategory": {
                                                  "controlCategoryName": "ISMS 1"
                                                }
                                              }
                                            ]
                                        }
                                    ]
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "204", description = "No control categories found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/get-all-control-categories")
    public ResponseEntity<List<ControlCategoryDTO>> getAllControls()
    {
        log.info("Received request to get all control categories");
        List<ControlCategoryDTO> controls=controlCategoryService.getAllControlCategories();
        log.info("All control categories fetched successfully:{}",controls.size());
        return  ResponseEntity.status(HttpStatus.OK).body(controls);
    }


    //ADD NEW CONTROL CATEGORY
    @Operation(summary = "Add a new control category", description = "Creates a new control category entry in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Control Category created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ControlCategoryDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "controlCategoryId": "c97d8605-3198-450e-a10d-acdcde222dbb",
                                        "controlCategoryName": "string",
                                        "controlList": []
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "400", description = "Invalid input provided", content = @Content),
            @ApiResponse(responseCode = "409", description = "Control Category already exists", content = @Content)
    })
    @PreAuthorize("hasRole('Admin')")
    @PostMapping("/add-control-category")
    public ResponseEntity<ControlCategoryDTO> addControlCategory(@RequestBody ControlCategoryCreateDTO controlCategoryCreateDTO) throws ControlCategoryAlreadyExistsException {
        log.info("Received request to add new control category: {}", controlCategoryCreateDTO);
        ControlCategoryDTO savedControlCategory = controlCategoryService.addControlCategory(controlCategoryCreateDTO);
        log.info("Control Category added successfully: {}", savedControlCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedControlCategory);
    }

    //UPDATE EXISTING CONTROL CATEGORY
    @Operation(summary = "Update existing control categories", description = "Update  existing control categories entry in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Control Categories updated successfully", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ControlCategoryDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "One or more Control Categories Not Found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PreAuthorize("hasRole('Admin')")
    @PutMapping("/update-control-category")
    public ResponseEntity<List<ControlCategoryDTO>> updateControlCategories(@RequestBody List<ControlCategoryUpdateDTO> controlCategoryUpdates){
        log.info("Received request to update existing control categories: {}", controlCategoryUpdates);
        List<ControlCategoryDTO> updatedControlCategories=controlCategoryService.updateControlCategories(controlCategoryUpdates);
        log.info("Control Categories  updated successfully: {}", updatedControlCategories);
        return  ResponseEntity.status(HttpStatus.OK).body(updatedControlCategories);
    }

    @PostMapping("/add-edit-control-category")
    public ResponseEntity<List<ControlCategoryDTO>> addEditControlCategory(@RequestBody  List<ControlCategoryCreateDTO> controlCategoryCreateDTOList) throws ControlCategoryAlreadyExistsException, ControlCategoryNotFoundException {
        List<ControlCategoryDTO> controlCategoryDTOList = controlCategoryService.addEditControlCategory(controlCategoryCreateDTOList);
        return ResponseEntity.status(HttpStatus.CREATED).body(controlCategoryDTOList);
    }

    //UPLOAD FUNCTIONALITY TO INSERT DATA THROUGH CSV FILES
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ControlCategoryDTO>> uploadFile(@RequestParam("file") MultipartFile file) throws IOException, ControlCategoryAlreadyExistsException, ControlCategoryNotFoundException {
        if(file.isEmpty())
        {
            return  ResponseEntity.badRequest().body(null);
        }
        List<ControlCategoryCreateDTO>  controlCreateDTO=controlCategoryService.parseCSVFile(file);
        List<ControlCategoryDTO> result=controlCategoryService.addEditControlCategory(controlCreateDTO);
        return  ResponseEntity.ok(result);
    }
}
