package com.iscp.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RoleNotFoundException extends ServiceException{
    public RoleNotFoundException() {
        super(ErrorTag.ROLE_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}

