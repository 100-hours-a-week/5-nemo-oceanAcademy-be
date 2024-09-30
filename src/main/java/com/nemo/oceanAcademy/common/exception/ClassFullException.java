package com.nemo.oceanAcademy.common.exception;

public class ClassFullException extends RuntimeException {
    private final String messageKor;
    private final String messageEng;
    private final String data;

    public ClassFullException(String messageKor, String messageEng) {
        super(messageEng);
        this.data = "error"; // 접근 권한 사용자 역할 오류
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