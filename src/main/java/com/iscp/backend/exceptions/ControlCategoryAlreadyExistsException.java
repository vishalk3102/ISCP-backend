package com.iscp.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ControlCategoryAlreadyExistsException extends  ServiceException {
    public ControlCategoryAlreadyExistsException() {
       super(ErrorTag.CONTROL_CATEGORY_ALREADY_EXISTS, HttpStatus.CONFLICT);
    }
}
