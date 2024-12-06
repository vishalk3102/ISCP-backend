package com.iscp.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class SecurityComplianceNotFoundException extends ServiceException{
    public SecurityComplianceNotFoundException() {
        super(ErrorTag.SECURITY_COMPLIANCE_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}