package com.iscp.backend.dto;

import com.iscp.backend.exceptions.ErrorTag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private  final ErrorTag errorTag;
    private  final String message;
}
