package com.backend.feature_flag_platform.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message ){
        super(message);
    }
}
