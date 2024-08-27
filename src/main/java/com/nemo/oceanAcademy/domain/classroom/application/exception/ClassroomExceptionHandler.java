package com.nemo.oceanAcademy.domain.classroom.application.exception;
import com.nemo.oceanAcademy.common.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class ClassroomExceptionHandler {

    @ExceptionHandler(ClassroomNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleClassroomNotFound(ClassroomNotFoundException ex) {
        return ErrorResponse.error("강의실을 찾을 수 없습니다.", "Classroom not found", HttpStatus.NOT_FOUND, null);
    }
}
