package com.nemo.oceanAcademy.domain.classroom.application.exception;

public class ClassroomNotFoundException extends RuntimeException {
    private final String messageKor;
    private final String messageEng;

    // 두 개의 파라미터를 받는 생성자 정의
    public ClassroomNotFoundException(String messageKor, String messageEng) {
        super(messageEng);  // 상위 클래스에는 영어 메시지를 전달
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
}
