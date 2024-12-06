package com.iscp.backend.controllers;

import com.iscp.backend.dto.EvidenceDTO;
import com.iscp.backend.exceptions.ChecklistNotFoundException;
import com.iscp.backend.exceptions.SecurityComplianceNotFoundException;
import com.iscp.backend.exceptions.UserNotFoundException;
import com.iscp.backend.models.Evidence;
import com.iscp.backend.services.EvidenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Controller for handling evidence-related operations.
 */
@RestController
@RequestMapping(path = EvidenceController.PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
public class EvidenceController {

    private final EvidenceService evidenceService;

    public final static String PATH = "/api/evidence";


    /**
     * Upload and save evidences associated with a specific checklist and security compliance.
     *
     * @param files an array of MultipartFile to be uploaded.
     * @param checklistName the name of the checklist to associate with the evidence.
     * @param token JWT token used for authentication.
     * @param securityId the ID of the security compliance to associate with the evidence.
     * @return a list of {@link EvidenceDTO} representing the saved evidence.
     * @throws IOException if an error occurs during file upload.
     * @throws ChecklistNotFoundException if no checklist exists with the given name.
     * @throws SecurityComplianceNotFoundException if no security compliance exists with the given ID.
     */
    @Operation(summary = "Upload Evidence", description = "Upload Evidence")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evidence uploaded successfully",
                    content = @Content(mediaType = "multipart/form-data")),
            @ApiResponse(responseCode = "204", description = "No file uploaded", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/upload")
    public ResponseEntity<List<EvidenceDTO>> uploadEvidence(@RequestPart(value="files", required=true) MultipartFile[] files, @RequestParam("checklistName") String checklistName, @RequestHeader (name="Authorization", required=false) String token, @RequestParam("securityId") String securityId) throws IOException, ChecklistNotFoundException, UserNotFoundException, SecurityComplianceNotFoundException {
        log.info("Received request to upload the evidence");

        //Call Service Method (saveEvidence) to save the Evidences belong to a SecurityCompliance
        List<EvidenceDTO> savedEvidenceDTOList = evidenceService.saveEvidence(files, checklistName, token, securityId);

        log.info("Evidence uploaded successfully");
        return ResponseEntity.status(HttpStatus.OK).body(savedEvidenceDTOList);
    }


    /**
     * View an evidence with the specified name.
     *
     * @param filename the name of the evidence file to be viewed.
     * @return a {@link Resource} representing the evidence.
     * @throws IOException if an error occurs while accessing the file.
     * @throws FileNotFoundException if the file does not exist or is not readable.
     */
    @Operation(summary = "View Evidence", description = "View Evidence")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File viewed",
                    content = @Content(mediaType = "multipart/form-data")),
            @ApiResponse(responseCode = "204", description = "No file found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/view/{filename:.+}")
    public ResponseEntity<Resource> viewEvidence(@PathVariable String filename) throws IOException {
        //Call method to get evidence by filename
        Evidence evidence = evidenceService.getEvidenceByFilename(filename);

        // Check if the evidence exists or not
        if (evidence == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        //Call Service Method to view the Evidence
        Resource resource = evidenceService.viewEvidence(filename);

        //Invoke Service method to get the Evidence type
        String contentType = evidenceService.getEvidenceType(filename);

        // Retrieve user ID from the Evidence object
        String userName = evidence.getUser().getName();

        log.info("Detected content type for {}: {}", filename, contentType);
        log.info("Serving file: {}", filename);
        log.info("File path: {}", resource.getURI());
        log.info("User Name: {}", userName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .header("X-User-Name", userName)
                .body(resource);
    }


    /**
     *  Download an evidence with the specified name.
     *
     * @param filename the name of the evidence file to be downloaded.
     * @return a {@link Resource} representing the evidence.
     * @throws IOException if an error occurs while accessing the file.
     */
    @Operation(summary = "Download Evidence", description = "Download Evidence")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Download the File",
                    content = @Content(mediaType = "*/*")),
            @ApiResponse(responseCode = "204", description = "No file found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadEvidence(@PathVariable String filename) throws IOException {
        //Call Service Method (ViewEvidence)
        Resource resource = evidenceService.viewEvidence(filename);

        //Call Service method to get the Evidence type
        String contentType = evidenceService.getEvidenceType(filename);

        log.info("Downloading file: {}", filename);
        log.info("Content type : {}", contentType);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}