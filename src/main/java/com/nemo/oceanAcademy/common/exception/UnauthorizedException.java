package com.nemo.oceanAcademy.common.exception;

public class UnauthorizedException extends RuntimeException {
    private final String messageKor;
    private final String messageEng;
    private final String data;

    // 두 개의 파라미터를 받는 생성자 정의
    public UnauthorizedException(String messageKor, String messageEng) {
        super(messageEng);
        this.data = "error";  // data 필드를 none으로 초기화
        this.messageKor = messageKor;
        this.messageEng = messageEng;
    }

    // 한국어 메시지를 반환하는 메서드
    public String getMessageKor() {
        return messageKor;
    }

    // 영어 메시지를 반환하는 메서드
    public String getMessageEng() {
        return messageEng;
    }

    // data를 반환하는 메서드
    public String getData() {
        return data;
    }
}
