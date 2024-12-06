package com.iscp.backend.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidCaptchaException extends ServiceException{
    public InvalidCaptchaException() {
        super(ErrorTag.INVALID_CAPTCHATOKEN,HttpStatus.BAD_REQUEST);
    }
}
