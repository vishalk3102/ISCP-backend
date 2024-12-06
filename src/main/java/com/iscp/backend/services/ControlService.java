package com.iscp.backend.services;

import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.ControlAlreadyExistsException;
import com.iscp.backend.exceptions.ControlCategoryNotFoundException;
import com.iscp.backend.exceptions.ControlNotFoundException;
import com.iscp.backend.models.Control;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ControlService {

    List<ControlDTO> getAllControls();

    List<ControlDTO> addEditControl(List<ControlCreateDTO> controlCreateDTOList) throws ControlAlreadyExistsException, ControlCategoryNotFoundException, ControlNotFoundException;

    ControlDTO addControl(ControlCreateDTO controlCreateDTO) throws ControlCategoryNotFoundException, ControlAlreadyExistsException;
    List<ControlDTO> updateControls(List<ControlUpdateDTO> controlUpdateDTO) throws ControlNotFoundException;
    Control updateSingleControl(ControlUpdateDTO controlUpdateDTO) throws ControlNotFoundException;
    List<ControlDTO> getControlFromControlCategory(String controlCategoryName) throws ControlCategoryNotFoundException;

    List<ControlCreateDTO> parseCSVFile(MultipartFile file) throws IOException;

    PaginatedResponse<ControlDTO> getFilterControlPaginated(ControlFilterDTO filterDTO);

    ByteArrayResource exportExcelControl(ControlFilterDTO filter) throws IOException;
}
