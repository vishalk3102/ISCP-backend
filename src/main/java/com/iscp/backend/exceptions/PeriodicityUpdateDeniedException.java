package com.iscp.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class PeriodicityUpdateDeniedException extends  ServiceException {
    public PeriodicityUpdateDeniedException() {
        super(ErrorTag.PERIODICITY_UPDATE_DENIED, HttpStatus.FORBIDDEN);
    }
}
