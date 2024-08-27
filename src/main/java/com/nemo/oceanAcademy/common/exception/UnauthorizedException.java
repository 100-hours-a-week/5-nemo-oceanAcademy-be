package com.nemo.oceanAcademy.common.exception;

public class UnauthorizedException extends RuntimeException {
    private final String messageKor;
    private final String messageEng;
    private final String data;

    public UnauthorizedException(String messageKor, String messageEng) {
        super(messageEng);
        this.data = "error";
        this.messageKor = messageKor;
        this.messageEng = messageEng;
    }

    public String getMessageKor() {
        return messageKor;
    }

    public String getMessageEng() {
        return messageEng;
    }

    public String getData() {
        return data;
    }
}
