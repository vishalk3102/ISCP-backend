package com.iscp.backend.services;

import com.iscp.backend.dto.*;
import com.iscp.backend.exceptions.FrameworkCategoryExistsException;
import com.iscp.backend.exceptions.FrameworkCategoryNotFoundException;
import com.iscp.backend.exceptions.FrameworkNotFoundException;

import java.util.List;


public interface FrameworkCategoryService {

    FrameworkCategoryDTO addFrameworkCategory(FrameworkCategoryCreateDTO frameworkCategoryCreateDTO) throws FrameworkCategoryExistsException;

    // To update framework category
    List<FrameworkCategoryDTO> updateFrameworkCategory(List<FrameworkCategoryUpdateDTO> frameworkCategoryUpdateDTOs) throws FrameworkCategoryNotFoundException, FrameworkCategoryExistsException;

    List<FrameworkCategoryDTO> getAllFrameworkCategories();
}
