package com.nemo.oceanAcademy.common.exception;

public class ResourceNotFoundException extends RuntimeException {
    private final String messageKor;
    private final String messageEng;

    public ResourceNotFoundException(String messageKor, String messageEng) {
        super(messageEng);
        this.messageKor = messageKor;
        this.messageEng = messageEng;
    }

    public String getMessageKor() {
        return messageKor;
    }

    public String getMessageEng() {
        return messageEng;
    }
}
