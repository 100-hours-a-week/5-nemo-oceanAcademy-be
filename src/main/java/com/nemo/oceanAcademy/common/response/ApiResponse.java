package com.nemo.oceanAcademy.common.response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ApiResponse {

    // 성공적인 응답 생성
    public static ResponseEntity<Map<String, Object>> success(String messageKor, String messageEng, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", data != null ? data : "success");  // data가 null인 경우 "success"으로 처리
        response.put("message_kor", messageKor);
        response.put("message_eng", messageEng);
        response.put("status", HttpStatus.OK.value());
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
