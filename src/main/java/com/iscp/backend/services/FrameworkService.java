package com.iscp.backend.services;

import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.FrameworkCategoryNotFoundException;
import com.iscp.backend.exceptions.FrameworkNotFoundException;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.util.List;


public interface FrameworkService {

    // To add framework
    FrameworkDTO addFramework(FrameworkCreateDTO frameworkCreateDTO) throws FrameworkCategoryNotFoundException;

    // To update framework
    List<FrameworkDTO> updateFramework(List<FrameworkUpdateDTO> frameworkUpdateDTOs) throws FrameworkNotFoundException,FrameworkCategoryNotFoundException ;

    List<FrameworkDTO> getAllFrameworks();

    List<FrameworkDTO> getFrameworkFromFrameworkCategory(String frameworkCategoryName) throws FrameworkCategoryNotFoundException;

    PaginatedResponse<FrameworkDTO> getFilterFrameworkPaginated(FrameworkFilterDTO filterDTO) ;

    List<FrameworkDTO> addEditFramework(List<FrameworkCreateDTO> frameworkCreateDTOList) throws FrameworkCategoryNotFoundException, FrameworkNotFoundException;

    ByteArrayResource exportExcelFramework(FrameworkFilterDTO filter) throws IOException;
}
