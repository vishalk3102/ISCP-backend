package com.iscp.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ChecklistAlreadyExistsException  extends  ServiceException{
    public ChecklistAlreadyExistsException() {
        super(ErrorTag.CHECKLIST_ALREADY_EXISTS, HttpStatus.CONFLICT);
    }
}

