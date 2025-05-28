package com.backend.api.common.exception;

public class EncryptException extends Throwable {
    private String message;
    public EncryptException(String msg) {
        super(msg);
    }
    public String getMessages() {
        return message;
    }
}
