package com.iscp.backend.controllers;

import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.ChecklistAlreadyExistsException;
import com.iscp.backend.exceptions.ChecklistNotFoundException;
import com.iscp.backend.exceptions.ControlNotFoundException;
import com.iscp.backend.services.ChecklistService;
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
@RequestMapping(path = ChecklistController.PATH,produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
public class ChecklistController {

    public static final String PATH ="/api/checklist" ;

    private final  ChecklistService checklistService;


    // GET CHECKLIST ACCORDING TO CHECKLIST NAME
    @Operation(summary = "Get checklist by name", description = "Fetch checklist details from database based on checklist name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checklist fetched successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChecklistDTO.class))),
            @ApiResponse(responseCode = "404", description = "Checklist not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid checklist name", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/get-checklist-details")
    public ResponseEntity<ChecklistDTO> getChecklistByName(@RequestBody ChecklistRequest checklistRequest) throws ChecklistNotFoundException {

        log.info("Received request to get checklist with name: {}", checklistRequest.getControlChecklist());
        ChecklistDTO checklist = checklistService.getChecklistByName(checklistRequest.getControlChecklist(),checklistRequest.getControlName());
        log.info("Checklist fetched successfully for name: {}", checklistRequest.getControlChecklist());
        return ResponseEntity.status(HttpStatus.OK).body(checklist);
    }



    //GET ALL CHECKLISTS
    @Operation(summary = "Get all checklists", description = "Fetch all checklists from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checklist fetched successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ChecklistDTO.class)))),
            @ApiResponse(responseCode = "204", description = "No Checklist found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/get-all-checklist")
    public ResponseEntity<List<ChecklistDTO>> getAllChecklists()
    {
        log.info("Received request to get all checklists");
        List<ChecklistDTO> checklists=checklistService.getAllChecklists();
        log.info("All Checklists fetched successfully:{}",checklists.size());
        return  ResponseEntity.status(HttpStatus.OK).body(checklists);
    }

    //GET ALL CHECKLIST FROM THE CONTROL
    @Operation(summary = "Get all checklist by control name",description = "get all checklist from database by control name")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200",description = "checklist fetched successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ChecklistDTO.class)))),
            @ApiResponse(responseCode = "204", description = "No Checklist found", content = @Content)
    })
    @GetMapping("/get-checklist-from-control/{controlName}")
    public ResponseEntity<List<ChecklistDTO>> getAllCheckListByControlName(@PathVariable String controlName) throws ControlNotFoundException {
        log.info("Received request to get all checklist associated with control name {}",controlName);
        List<ChecklistDTO> checklistDTOList = checklistService.getChecklistFromControl(controlName);
        log.info("All Checklists fetched successfully by control: {}",checklistDTOList);
        return ResponseEntity.status(HttpStatus.OK).body(checklistDTOList);
    }


    // GET ALL CHECKLIST ASSOCIATED TO LIST OF  CONTROLS
    @PostMapping("/get-checklist-from-control")
    public ResponseEntity<List<ChecklistDTO>> getAllCheckListByControlNameList(@RequestBody List<String> controlName) throws ControlNotFoundException {
        log.info("Received request to get checklists associated with control name list {}",controlName);
        List<ChecklistDTO> checklistDTOList = checklistService.getChecklistFromControlList(controlName);
        log.info("All Checklists fetched successfully by control name: {}",checklistDTOList);
        return ResponseEntity.status(HttpStatus.OK).body(checklistDTOList);
    }


    // ADD/UPDATE CHECKLIST
    @Operation(summary = "Add and edit checklist", description = "Creates a new checklist entry in the database and if present then update it")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "checklist created",content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = ChecklistDTO.class))
            })
    })
    @PostMapping("/add-edit-checklist")
    public ResponseEntity<List<ChecklistDTO>> addEditChecklist(@RequestBody  List<ChecklistCreateDTO> checklistCreateDTOList) throws ControlNotFoundException, ChecklistAlreadyExistsException, ChecklistNotFoundException {
        List<ChecklistDTO> checklistDTOList = checklistService.addEditCheckList(checklistCreateDTOList);
        return ResponseEntity.status(HttpStatus.CREATED).body(checklistDTOList);
    }

    //GET ALL FILTERED CHECKLIST
    @Operation(summary = "Get paginated Checklist using filters", description = "Fetch  Checklist using filters from the database as per pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated Checklist fetched successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ChecklistDTO.class)))),
            @ApiResponse(responseCode = "204", description = "No Checklist found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/get-filtered-checklist-paginated")
    public ResponseEntity<PaginatedResponse<ChecklistDTO>> getAllChecklistFiltered(@RequestBody ChecklistFilterDTO checklistFilterDTO)
    {
        log.info("Received request to get all Checklist ");
        PaginatedResponse<ChecklistDTO> checklistDTOPaginatedResponse = checklistService.getFilterChecklistPaginated(checklistFilterDTO);
        log.info("All Checklist  fetched successfully:{}",checklistDTOPaginatedResponse.getTotalElements());
        return  ResponseEntity.status(HttpStatus.OK).body(checklistDTOPaginatedResponse);
    }


    //EXPORT DATA TO EXCEL
    @Operation(summary = "To export data to excel sheet", description = "To export table data to excel sheet of Checklist")
    @PostMapping("/export-excel")
    public ResponseEntity<ByteArrayResource> exportsExcelChecklist(@RequestBody ChecklistFilterDTO filter) throws IOException {
        log.info("Received request to export checklist table data to excel sheet");

        ByteArrayResource resource = checklistService.exportExcelChecklist(filter);
        log.info("Exported checklist data to Excel successfully");

        String fileName = "checklist.xlsx";

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(resource.contentLength())
                .body(resource);

    }

    //UPLOAD FUNCTIONALITY TO INSERT DATA THROUGH CSV FILES
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ChecklistDTO>> uploadFile(@RequestParam("file") MultipartFile file) throws ControlNotFoundException, ChecklistAlreadyExistsException, ChecklistNotFoundException, IOException {
        if(file.isEmpty())
        {
            return  ResponseEntity.badRequest().body(null);
        }
        List<ChecklistCreateDTO>  checklistCreateDTOList =checklistService.parseCSVFile(file);
        List<ChecklistDTO> result=checklistService.addEditCheckList(checklistCreateDTOList);
        return  ResponseEntity.ok(result);
    }

}
