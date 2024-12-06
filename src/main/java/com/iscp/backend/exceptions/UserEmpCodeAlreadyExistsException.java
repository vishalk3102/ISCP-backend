package com.iscp.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class UserEmpCodeAlreadyExistsException extends  ServiceException{
    public UserEmpCodeAlreadyExistsException() {
        super(ErrorTag.USER_EMPCODE_ALREADY_EXISTS, HttpStatus.CONFLICT);
    }
}