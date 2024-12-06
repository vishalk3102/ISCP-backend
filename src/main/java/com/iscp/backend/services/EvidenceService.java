package com.iscp.backend.services;

import com.iscp.backend.dto.EvidenceDTO;
import com.iscp.backend.exceptions.ChecklistNotFoundException;
import com.iscp.backend.exceptions.SecurityComplianceNotFoundException;
import com.iscp.backend.exceptions.UserNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import com.iscp.backend.models.Evidence;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Service Interface for managing Evidences.
 */
public interface EvidenceService {

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
    List<EvidenceDTO> saveEvidence(MultipartFile[] files, String checklistName, String token, String securityId) throws IOException, ChecklistNotFoundException, UserNotFoundException, SecurityComplianceNotFoundException;


    /**
     * Retrieves a resource representing an evidence with the specified name.
     *
     * @param filename the name of the evidence file to be viewed.
     * @return a {@link Resource} representing the evidence.
     * @throws IOException if an error occurs while accessing the file.
     * @throws FileNotFoundException if the file does not exist or is not readable.
     */
    Resource viewEvidence(String filename) throws IOException;


    /**
     * Determines the type of the evidence based on its name and contents.
     *
     * @param filename the name of the evidence file for which to determine the type.
     * @return a String representing the type of the file.
     * @throws IOException if an error occurs while reading the file.
     */
    String getEvidenceType(String filename) throws IOException;


    /**
     * Retrieves an {@link Evidence} object by its name.
     *
     * @param filename the name of the evidence to retrieve.
     * @return an {@link Evidence} object associated with the given filename, or null if no evidence is found.
     */
    Evidence getEvidenceByFilename(String filename);
}
