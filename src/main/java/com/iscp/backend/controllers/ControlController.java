package com.iscp.backend.controllers;


import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.ControlAlreadyExistsException;
import com.iscp.backend.exceptions.ControlCategoryNotFoundException;
import com.iscp.backend.exceptions.ControlNotFoundException;
import com.iscp.backend.services.ControlService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = ControlController.PATH,produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
public class ControlController {

    public static final String PATH ="/api/control" ;

    private final  ControlService controlService;


    //GET ALL CONTROLS
    @Operation(summary = "Get all controls", description = "Fetch all controls from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Controls fetched successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ControlDTO.class)))),
            @ApiResponse(responseCode = "204", description = "No controls found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/get-all-controls")
    public ResponseEntity<List<ControlDTO>> getAllControls()
    {
        log.info("Received request to get all controls");
        List<ControlDTO> controls=controlService.getAllControls();
        log.info("All Controls fetched successfully:{}",controls.size());
        return  ResponseEntity.status(HttpStatus.OK).body(controls);
    }

    //GET LIST OF CONTROL FROM CONTROL CATEGORY
    @Operation(summary = "Get all controls from control category", description = "Fetch all controls from the database by control category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Controls fetched successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ControlDTO.class)))),
            @ApiResponse(responseCode = "204", description = "No control categories found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/control-from-control-category/{controlCategoryName}")
    public ResponseEntity<List<ControlDTO>> getControlFromControlCategory(@PathVariable String controlCategoryName) throws ControlCategoryNotFoundException {
        log.info("Received request to get all controls by using control category name");
        List<ControlDTO> controlDTOList = controlService.getControlFromControlCategory(controlCategoryName);
        log.info("All Controls fetched successfully using control category name:{}",controlDTOList);
        return ResponseEntity.status(HttpStatus.OK).body(controlDTOList);
    }

    // ADD/UPDATE CONTROL
    @Operation(summary = "Add and edit control", description = "Creates a new control entry in the database and if present then update it")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "Control created",content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = ControlDTO.class))
            }),
            @ApiResponse(responseCode = "409",description = "Control Category Not Found",content = @Content)
    })
    @PostMapping("/add-edit-control")
    public ResponseEntity<List<ControlDTO>> addEditControl(@RequestBody  List<ControlCreateDTO> controlCreateDTOList) throws ControlNotFoundException, ControlAlreadyExistsException, ControlCategoryNotFoundException {
        List<ControlDTO> controlDTOList = controlService.addEditControl(controlCreateDTOList);
        return ResponseEntity.status(HttpStatus.CREATED).body(controlDTOList);
    }

    //SEARCH FUNCTIONALITY
    @Operation(summary = "Get paginated control using filters", description = "Fetch  control using filters from the database as per pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated control fetched successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ControlDTO.class)))),
            @ApiResponse(responseCode = "204", description = "No control found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/get-filtered-control-paginated")
    public ResponseEntity<PaginatedResponse<ControlDTO>> getAllControlFiltered(@RequestBody ControlFilterDTO controlFilterDTO)
    {
        log.info("Received request to get all control ");
        PaginatedResponse<ControlDTO> controlDTOPaginatedResponse = controlService.getFilterControlPaginated(controlFilterDTO);
        log.info("All control fetched successfully:{}",controlDTOPaginatedResponse.getTotalElements());
        return  ResponseEntity.status(HttpStatus.OK).body(controlDTOPaginatedResponse);
    }


    //EXPORT DATA TO EXCEL
    @Operation(summary = "To export data to excel sheet", description = "To export table data to excel sheet of Control")
    @PostMapping("/export-excel")
    public ResponseEntity<ByteArrayResource> exportsExcelControl(@RequestBody ControlFilterDTO filter) throws IOException {
        log.info("Received request to export control table data to excel sheet");

        ByteArrayResource resource = controlService.exportExcelControl(filter);
        log.info("Exported control data to Excel successfully");

        String fileName = "control.xlsx";

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(resource.contentLength())
                .body(resource);

    }


    //UPLOAD FUNCTIONALITY TO INSERT DATA THROUGH CSV FILES
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ControlDTO>> uploadFile(@RequestParam("file") MultipartFile file) throws ControlNotFoundException, ControlAlreadyExistsException, ControlCategoryNotFoundException, IOException {
        if(file.isEmpty())
        {
            return  ResponseEntity.badRequest().body(null);
        }
        List<ControlCreateDTO>  controlCreateDTO=controlService.parseCSVFile(file);
        List<ControlDTO> result=controlService.addEditControl(controlCreateDTO);
        return  ResponseEntity.ok(result);
    }

}


