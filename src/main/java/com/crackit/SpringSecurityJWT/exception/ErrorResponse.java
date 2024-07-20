package com.crackit.SpringSecurityJWT.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String errorCode;
    private Map<String, String>errors;

    public ErrorResponse(LocalDateTime timestamp, int status, String error, Map<String, String> errors) {
        this.timestamp = timestamp;
        this.status = status;
        this.errorCode = error;
        this.errors = errors;
    }
}

