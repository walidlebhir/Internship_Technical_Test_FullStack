package com.backend.feature_flag_platform.exception;

public class InvalidStrategyConfigException extends  RuntimeException {
    public InvalidStrategyConfigException(String message){
        super(message);
    }
}
