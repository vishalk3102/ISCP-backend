package com.iscp.backend.services;

import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.ChecklistAlreadyExistsException;
import com.iscp.backend.exceptions.ChecklistNotFoundException;
import com.iscp.backend.exceptions.ControlNotFoundException;
import com.iscp.backend.models.Checklist;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface ChecklistService {

    //FUNCTION TO GET  CHECKLIST DETAILS
    ChecklistDTO getChecklistByName(String checklistName, String controlName) throws ChecklistNotFoundException;

    List<ChecklistDTO> getAllChecklists();
    List<ChecklistDTO> addEditCheckList(List<ChecklistCreateDTO> checklistCreateDTOList) throws ChecklistNotFoundException, ChecklistAlreadyExistsException, ControlNotFoundException;

    ChecklistDTO addChecklist(ChecklistCreateDTO checklistCreateDTO) throws ChecklistAlreadyExistsException, ControlNotFoundException;

    List<ChecklistDTO> updateChecklists(List<ChecklistUpdateDTO> checklistUpdateDTOs) throws ChecklistNotFoundException, ControlNotFoundException;

    Checklist updateSingleChecklist(ChecklistUpdateDTO checklistUpdateDTO) throws ChecklistNotFoundException, ControlNotFoundException;

    List<ChecklistDTO> getChecklistFromControl(String controlName) throws ControlNotFoundException;

    List<ChecklistDTO> getChecklistFromControlList(List<String> controlName) throws ControlNotFoundException;

    PaginatedResponse<ChecklistDTO> getFilterChecklistPaginated(ChecklistFilterDTO filterDTO);

    ByteArrayResource exportExcelChecklist(ChecklistFilterDTO filter) throws IOException;

    List<ChecklistCreateDTO> parseCSVFile(MultipartFile file)  throws IOException;

}
