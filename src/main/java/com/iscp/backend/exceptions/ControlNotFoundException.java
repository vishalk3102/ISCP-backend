package com.iscp.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ControlNotFoundException extends  ServiceException{
    public ControlNotFoundException() {
        super(ErrorTag.CONTROL_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}
