package com.nemo.oceanAcademy.common.response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponse {

    // 에러 응답 생성
    public static ResponseEntity<Map<String, Object>> error(String messageKor, String messageEng, HttpStatus status, Object data) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("data", data != null ? data : "error");  // data가 null인 경우 "error"으로 처리
        errorResponse.put("message_kor", messageKor);
        errorResponse.put("message_eng", messageEng);
        errorResponse.put("status", status.value());
        errorResponse.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(status).body(errorResponse);
    }
}
