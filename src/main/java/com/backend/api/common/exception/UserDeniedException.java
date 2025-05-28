package com.backend.api.common.exception;

public class UserDeniedException extends RuntimeException {
    private String message;
    public UserDeniedException(String msg) {
        super(msg);
    }
    public String getMessages() {
        return message;
    }
}
