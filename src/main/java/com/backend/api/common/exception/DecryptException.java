package com.backend.api.common.exception;

public class DecryptException extends Throwable {
    private String message;
    public DecryptException(String msg) {
        super(msg);
    }
    public String getMessages() {
        return message;
    }
}
