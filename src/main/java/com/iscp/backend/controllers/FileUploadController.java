package com.iscp.backend.controllers;


import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.*;
import com.iscp.backend.services.ChecklistService;
import com.iscp.backend.services.ControlCategoryService;
import com.iscp.backend.services.ControlService;
import com.iscp.backend.services.FileUploadService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = FileUploadController.PATH,produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
public class FileUploadController {
    public static final String PATH ="/api/master-upload" ;


    private final FileUploadService fileUploadService;

    private final ControlService controlService;

    private final ChecklistService checklistService;

    private final ControlCategoryService controlCategoryService;

    //UPLOAD FUNCTIONALITY TO INSERT DATA THROUGH CSV FILES
    @PostMapping(value = "/upload-excel-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MultiEntityUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) throws ControlNotFoundException, ControlAlreadyExistsException, ControlCategoryNotFoundException, ChecklistAlreadyExistsException, ChecklistNotFoundException, IOException, ControlCategoryAlreadyExistsException {
        MultiEntityUploadResponse response = new MultiEntityUploadResponse();
        if(file.isEmpty())
        {
            return  ResponseEntity.badRequest().body(null);
        }

        //Process Control Categories
        List<ControlCategoryCreateDTO> controlCategoryEntries = fileUploadService.parseControlCategoryEntries(file);
        List<ControlCategoryDTO> controlCategoryResults = controlCategoryService.addEditControlCategory(controlCategoryEntries);
        response.setControlCategoryResults(controlCategoryResults);

        List<ControlCreateDTO> controlEntries = fileUploadService.parseControlEntries(file);
        List<ControlDTO> controlResults = controlService.addEditControl(controlEntries);
        response.setControlResults(controlResults);

        List<ChecklistCreateDTO> checklistEntries = fileUploadService.parseChecklistEntries(file);
        List<ChecklistDTO> checklistResults = checklistService.addEditCheckList(checklistEntries);
        response.setChecklistResults(checklistResults);

        return  ResponseEntity.ok(response);
    }
}
