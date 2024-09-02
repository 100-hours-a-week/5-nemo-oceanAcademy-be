package com.nemo.oceanAcademy.domain.chat.application.exception;

public class UnauthorizedException extends RuntimeException {
    private final String errorCode;

    private UnauthorizedException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public static UnauthorizedException of(String errorCode, String message) {
        return new UnauthorizedException(errorCode, message);
    }
}
