package com.iscp.backend.services;


import com.iscp.backend.dto.ChecklistCreateDTO;
import com.iscp.backend.dto.ControlCategoryCreateDTO;
import com.iscp.backend.dto.ControlCreateDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileUploadService {
    //FUNCTION TO CREATE CONTROL CATEGORY ENTITY DTO FROM EXCEL CONTENT
    List<ControlCategoryCreateDTO> parseControlCategoryEntries(MultipartFile file) throws IOException;

    List<ControlCreateDTO> parseControlEntries(MultipartFile file) throws IOException;

    List<ChecklistCreateDTO> parseChecklistEntries(MultipartFile file) throws IOException;
}
