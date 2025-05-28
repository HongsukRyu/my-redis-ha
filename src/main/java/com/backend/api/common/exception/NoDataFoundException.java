package com.backend.api.common.exception;

public class NoDataFoundException extends RuntimeException {
    private String message;
    public NoDataFoundException(String msg) {
        super(msg);
    }
    public String getMessages() {
        return message;
    }
}
