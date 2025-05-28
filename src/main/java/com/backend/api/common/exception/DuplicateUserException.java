package com.backend.api.common.exception;

public class DuplicateUserException extends RuntimeException {
    private String message;
    public DuplicateUserException(String msg) {
        super(msg);
    }
    public String getMessages() {
        return message;
    }
}
