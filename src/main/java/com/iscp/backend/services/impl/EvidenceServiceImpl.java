package com.iscp.backend.services.impl;

import com.iscp.backend.dto.EvidenceDTO;
import com.iscp.backend.exceptions.ChecklistNotFoundException;
import com.iscp.backend.exceptions.SecurityComplianceNotFoundException;
import com.iscp.backend.mapper.EvidenceMapper;
import com.iscp.backend.models.Checklist;
import com.iscp.backend.models.Evidence;
import com.iscp.backend.models.SecurityCompliance;
import com.iscp.backend.models.Users;
import com.iscp.backend.repositories.ChecklistRepository;
import com.iscp.backend.repositories.EvidenceRepository;
import com.iscp.backend.repositories.SecurityComplianceRepository;
import com.iscp.backend.repositories.UsersRepository;
import com.iscp.backend.security.JwtHelper;
import com.iscp.backend.services.EvidenceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the {@link EvidenceService} interface for managing Evidences.
 */
@Service
@Slf4j
public class EvidenceServiceImpl implements EvidenceService {

    private final EvidenceRepository evidenceRepository;

    private final EvidenceMapper evidenceMapper;

    private final ChecklistRepository checklistRepository;

    private final JwtHelper jwtHelper;

    private final UsersRepository userRepository;

    private final SecurityComplianceRepository securityComplianceRepository;

    @Value("${upload_directory}")
    private final String uploadDir;

    public EvidenceServiceImpl(EvidenceRepository evidenceRepository, EvidenceMapper evidenceMapper, ChecklistRepository checklistRepository, JwtHelper jwtHelper, UsersRepository userRepository, SecurityComplianceRepository securityComplianceRepository, @Value("${upload_directory}") String uploadDir) {
        this.evidenceRepository = evidenceRepository;
        this.evidenceMapper = evidenceMapper;
        this.checklistRepository = checklistRepository;
        this.jwtHelper = jwtHelper;
        this.userRepository = userRepository;
        this.securityComplianceRepository = securityComplianceRepository;
        this.uploadDir =Paths.get(uploadDir).toAbsolutePath().normalize().toString();
    }


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
    @Override
    public List<EvidenceDTO> saveEvidence(MultipartFile[] files, String checklistName, String token, String securityId) throws IOException, ChecklistNotFoundException, SecurityComplianceNotFoundException {
        //Check if provided token is not null and starts with Bearer
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        //Fetch username(EmailId) from token using jwtHelper
        String username = jwtHelper.getUsernameFromToken(token);
        log.info("username :{}",username);

        //Check if checklist exist with the given name
        Optional<Checklist> checklistOpt = checklistRepository.findByControlChecklist(checklistName);
        if (checklistOpt.isEmpty()) {
            log.error("Checklist Not found with name: {}", checklistName);
            throw new ChecklistNotFoundException();
        }

        //Check if SecurityCompliance exists for given securityID
        Optional<SecurityCompliance> securityComplianceOpt=securityComplianceRepository.findBySecurityId(securityId);
        if(securityComplianceOpt.isEmpty()){
            log.error("SecurityCompliance Not found with Id: {}", securityId);
            throw new SecurityComplianceNotFoundException();
        }

        //Retrieves the checklist and security compliance object
        Checklist checklist = checklistOpt.get();
        SecurityCompliance securityCompliance=securityComplianceOpt.get();

        //Find User with the given username(Email Id)
        Users user=userRepository.findByUserEmailId(username);
        log.info("user name :{}" , user);

        List<EvidenceDTO> evidenceDTOList = new ArrayList<>();

        for(MultipartFile file:files){
            //Get the original file name and clean the path
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            //Get file type
            String fileType = file.getContentType();
            //Get storage path for the file
            Path fileReference = Paths.get(uploadDir + fileName);
            //create directory if it doesn't exist
            Files.createDirectories(fileReference.getParent());
            //If a file already exist at given location, then replace it with the new file
            Files.copy(file.getInputStream(), fileReference, StandardCopyOption.REPLACE_EXISTING);

            //create a new evidence entity
            Evidence evidence = new Evidence();
            //Set the attributes of the evidence entity
            evidence.setFileName(fileName);
            evidence.setFileReference(fileReference.toString());
            evidence.setFileType(fileType);
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            evidence.setTimestamp(currentTimestamp);

            evidence.setChecklist(checklist);
            evidence.setUser(user);
            evidence.setSecurityCompliance(securityCompliance);

            //save the entity to the database
            Evidence savedEvidence = evidenceRepository.save(evidence);
            //Convert the Entity to DTO
            EvidenceDTO evidenceDTO = evidenceMapper.toEvidenceDto(savedEvidence);
            log.info("Evidence :{}", evidenceDTO);

            //Store the Evidence DTO into the list
            evidenceDTOList.add(evidenceDTO);
        }

        //Set EvidenceComment as completed if evidence saved successfully
        securityCompliance.setEvidenceComments("completed");
        securityComplianceRepository.save(securityCompliance);

        //Return the list of EvidenceDTO
        return evidenceDTOList;
    }


    /**
     * Retrieves a resource representing an evidence with the specified name.
     *
     * @param filename the name of the evidence file to be viewed.
     * @return a {@link Resource} representing the evidence.
     * @throws IOException if an error occurs while accessing the file.
     * @throws FileNotFoundException if the file does not exist or is not readable.
     */
    @Override
    public Resource viewEvidence(String filename) throws IOException{
        //Get the location of file by resolving filename
        Path file = Paths.get(uploadDir).resolve(filename).normalize();
        Resource resource = new PathResource(file);

        //If file with that name doesn't exist and is not readable, then throw an Exception
        if (!resource.exists() || !resource.isReadable()) {
            throw new FileNotFoundException("File not found with name: " + filename);
        }
        //return the resource representing the evidence
        return resource;
    }


    /**
     * Determines the type of the evidence based on its name and contents.
     *
     * @param filename the name of the evidence file for which to determine the type.
     * @return a String representing the type of the file.
     * @throws IOException if an error occurs while reading the file.
     */
    @Override
    public String getEvidenceType(String filename) throws IOException {
        //If file is of type mp4, then explicitly check it
        if (filename.endsWith(".mp4")) {
            return "video/mp4";
        }

        if (filename.endsWith(".mp3")) {
            return "audio/mpeg";
        }

        if (filename.endsWith(".doc") || filename.endsWith(".docx")) {
            return "application/msword";
        }

        //Create a Tika instance to detect file type based on content
        Tika tika = new Tika();

        // Read the file to determine its type
        Path path = Paths.get(uploadDir, filename);
        byte[] fileBytes = Files.readAllBytes(path);

        //return the file type
        return tika.detect(fileBytes);
    }


    /**
     * Retrieves an {@link Evidence} object by its name.
     *
     * @param filename the name of the evidence to retrieve.
     * @return an {@link Evidence} object associated with the given filename, or null if no evidence is found.
     */
    @Override
    public Evidence getEvidenceByFilename(String filename) {
        //Retrieves the Evidence from the repository
        return evidenceRepository.findByFileName(filename);
    }
}
