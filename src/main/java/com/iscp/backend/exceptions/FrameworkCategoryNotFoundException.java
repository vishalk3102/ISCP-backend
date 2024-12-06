package com.iscp.backend.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class FrameworkCategoryNotFoundException extends ServiceException{
    public FrameworkCategoryNotFoundException() {
        super(ErrorTag.FRAMEWORK_CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}
