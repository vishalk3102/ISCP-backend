package com.iscp.backend.services;

import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.ControlCategoryAlreadyExistsException;
import com.iscp.backend.exceptions.ControlCategoryNotFoundException;
import com.iscp.backend.exceptions.ControlNotFoundException;
import com.iscp.backend.models.Control;
import com.iscp.backend.models.ControlCategory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ControlCategoryService {
    ControlCategoryDTO getControlCategoryById(String id) throws  ControlCategoryNotFoundException;

    List<ControlCategoryDTO> getAllControlCategories() ;
    ControlCategoryDTO addControlCategory(ControlCategoryCreateDTO controlCategoryCreateDTO) throws  ControlCategoryAlreadyExistsException;
    List<ControlCategoryDTO> updateControlCategories(List<ControlCategoryUpdateDTO> controlCategoryUpdateDTOS) ;
    ControlCategory updateSingleControlCategory(ControlCategoryUpdateDTO controlCategoryUpdateDTO) throws ControlCategoryNotFoundException;
    List<ControlCategoryDTO> addEditControlCategory(List<ControlCategoryCreateDTO> controlCategoryCreateDTOList) throws ControlCategoryAlreadyExistsException, ControlCategoryNotFoundException;

    List<ControlCategoryCreateDTO> parseCSVFile(MultipartFile file) throws IOException;
}
