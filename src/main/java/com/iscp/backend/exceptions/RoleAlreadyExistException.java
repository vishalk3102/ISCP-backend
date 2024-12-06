package com.iscp.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.CONFLICT)
public class RoleAlreadyExistException extends  ServiceException{
    public RoleAlreadyExistException() {
        super(ErrorTag.ROLE_ALREADY_EXISTS, HttpStatus.CONFLICT);
    }
}
