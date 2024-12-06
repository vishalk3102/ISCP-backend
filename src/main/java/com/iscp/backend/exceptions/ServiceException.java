package com.iscp.backend.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public abstract class ServiceException extends  Exception{

    //Error tag associated with the exception.
    private final ErrorTag errorTag;

    //Status code associated with the exception.
    private final int statusCode;

    //Status code reason phrase associated with the exception.
    private final String reasonPhrase;

    public ServiceException(ErrorTag errorTag, HttpStatus status) {
        super(errorTag.getDescription());
        this.errorTag = errorTag;
        this.statusCode = status.value();
        this.reasonPhrase = status.getReasonPhrase();
    }
}
