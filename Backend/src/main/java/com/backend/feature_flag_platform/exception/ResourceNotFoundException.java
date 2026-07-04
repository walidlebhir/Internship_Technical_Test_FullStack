package com.backend.feature_flag_platform.exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message ){
        super(message);
    }
}
