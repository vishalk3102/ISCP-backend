package com.iscp.backend.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorTag {
    FRAMEWORK_NOT_FOUND("Framework Not Found"),
    CONTROL_NOT_FOUND("Control Not Found"),
    CHECKLIST_NOT_FOUND("Checklist Not Found"),
    USER_NOT_FOUND("User Not Found"),
    ROLE_NOT_FOUND("Role Not Found"),
    FRAMEWORK_CATEGORY_NOT_FOUND("Framework Category Not Found"),
    FRAMEWORK_CATEGORY_ALREADY_EXISTS("Framework Category Already Exists"),
    CONTROL_CATEGORY_NOT_FOUND("Control Category Not Found"),
    DEPARTMENT_NOT_FOUND("Department Not Found"),
    PERMISSION_NOT_FOUND("Permission Not Found"),
    CONTROL_CATEGORY_ALREADY_EXISTS("Control Category Already Exists"),
    CONTROL__ALREADY_EXISTS("Control Already Exists"),
    CHECKLIST_ALREADY_EXISTS("Checklist Already Exists"),
    ROLE_ALREADY_EXISTS("Role Already Exists"),
    SECURITY_COMPLIANCE_NOT_FOUND("Security Compliance Not Found"),
    BAD_CREDENTIALS("Bad Credentials"),
    USER_EMAIL_ALREADY_EXISTS("User Email Already Exist"),
    USER_EMPCODE_ALREADY_EXISTS("User EmpCode Already Exists"),
    PERIODICITY_UPDATE_DENIED("Periodicity Update Denied"),
    INVALID_CAPTCHATOKEN("Invalid captcha token");
    private final String tag;
    private final String description;

    ErrorTag(String tag) {
        this.tag = tag;
        this.description = null;
    }
}

