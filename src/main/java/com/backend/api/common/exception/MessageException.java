package com.backend.api.common.exception;

public class MessageException extends RuntimeException {
    private String message;
    public MessageException(String msg) {
        super(msg);
    }
    public String getMessages() {
        return message;
    }
}
