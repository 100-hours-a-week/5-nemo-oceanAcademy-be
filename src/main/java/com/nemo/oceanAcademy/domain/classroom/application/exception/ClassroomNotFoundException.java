package com.nemo.oceanAcademy.domain.classroom.application.exception;

public class ClassroomNotFoundException extends RuntimeException {
    private final String messageKor;
    private final String messageEng;

    public ClassroomNotFoundException(String messageKor, String messageEng) {
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
