package com.iscp.backend.exceptions;

import org.springframework.http.HttpStatus;

public class BadCredentialsException extends ServiceException{
    public BadCredentialsException() {
        super(ErrorTag.BAD_CREDENTIALS,HttpStatus.UNAUTHORIZED);
    }
}
