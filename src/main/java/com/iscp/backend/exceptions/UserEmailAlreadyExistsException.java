package com.iscp.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class UserEmailAlreadyExistsException extends  ServiceException{
    public UserEmailAlreadyExistsException() {
        super(ErrorTag.USER_EMAIL_ALREADY_EXISTS, HttpStatus.CONFLICT);
    }
}