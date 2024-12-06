package com.iscp.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.CONFLICT)
public class ControlAlreadyExistsException extends  ServiceException {
    public ControlAlreadyExistsException() {
        super(ErrorTag.CONTROL__ALREADY_EXISTS, HttpStatus.CONFLICT);
    }
}

