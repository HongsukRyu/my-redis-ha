package com.backend.api.common.exception;
public class MIMETypeException extends RuntimeException {
    private String message;
    public MIMETypeException(String msg) {
        super(msg);
    }
    public String getMessages() {
        return message;
    }
}
