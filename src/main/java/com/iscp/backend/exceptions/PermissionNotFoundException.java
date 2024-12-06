package com.iscp.backend.exceptions;

import org.springframework.http.HttpStatus;

public class PermissionNotFoundException extends ServiceException{
    public PermissionNotFoundException() {
        super(ErrorTag.PERMISSION_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}
