package com.iscp.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class DepartmentNotFoundException extends ServiceException{
    public DepartmentNotFoundException() {
        super(ErrorTag.DEPARTMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}

