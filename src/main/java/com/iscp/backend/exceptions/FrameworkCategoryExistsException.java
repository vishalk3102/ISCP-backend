package com.iscp.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FrameworkCategoryExistsException extends ServiceException {
    public FrameworkCategoryExistsException() {super(ErrorTag.FRAMEWORK_CATEGORY_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);}
}
