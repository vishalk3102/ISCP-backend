package com.iscp.backend.exceptions;

import ch.qos.logback.classic.Logger;
import com.iscp.backend.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Object> handleServiceException(ServiceException ex)
    {
        ErrorResponse response=new ErrorResponse(ex.getErrorTag(),ex.getErrorTag().getTag());
        return new ResponseEntity<>(response,HttpStatus.valueOf(ex.getStatusCode()));
    }
}
