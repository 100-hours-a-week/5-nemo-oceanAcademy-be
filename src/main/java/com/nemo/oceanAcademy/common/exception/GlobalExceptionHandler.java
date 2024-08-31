package com.nemo.oceanAcademy.common.exception;
import com.nemo.oceanAcademy.common.response.ApiResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 인증 실패 예외 처리 (JWT 인증 실패)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        Map<String, Object> errorResponse = createErrorResponse(
                "사용자 인증에 실패했습니다. (토큰 없음)",
                "Unauthorized request",
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                "error"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // 사용자 역할 권한 없음
    @ExceptionHandler(RoleUnauthorizedException.class)
    public ResponseEntity<?> handleRoleUnauthorizedException(RoleUnauthorizedException ex) {
        Map<String, Object> errorResponse = createErrorResponse(
                ex.getMessageKor(),
                ex.getMessageEng(),
                HttpStatus.I_AM_A_TEAPOT,  // 418 I AM A TEAPOT
                "Role Error",
                ex.getData()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.I_AM_A_TEAPOT);
    }

    // 이미 가입된 사용자 예외 처리
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        Map<String, Object> errorResponse = createErrorResponse(
                ex.getMessageKor(),
                ex.getMessageEng(),
                HttpStatus.CONFLICT,  // 409 Conflict
                "User Exists",
                ex.getData()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }


    // 요청 데이터 유효성 검사 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        Map<String, Object> errorResponse = createErrorResponse(
                "입력 값이 유효하지 않습니다. 요청 필드 타입을 확인해주세요.",
                "Invalid input data.",
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                "error"
        );
        errorResponse.put("invalid_fields", errors);  // 유효하지 않은 필드 목록
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 메서드 인자 타입 불일치 예외 처리 (URL 파라미터 타입 오류)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String errorMessageKor = String.format("'%s' 필드 값이 유효하지 않습니다.", ex.getName());
        String errorMessageEng = String.format("Invalid value for field '%s'.", ex.getName());
        Map<String, Object> errorResponse = createErrorResponse(errorMessageKor, errorMessageEng, HttpStatus.BAD_REQUEST, "Type Mismatch", "error");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 요청 데이터가 누락된 경우 - 거의 다 ResourceNotFoundException (custom) 으로 처리, 한국어, 영어 응답 제공
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> errorResponse = createErrorResponse(
                ex.getMessage(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                "Illegal Argument",
                "error"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 자원 찾을 수 없음 예외 처리
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, Object> errorResponse = createErrorResponse(
                ex.getMessageKor(),
                ex.getMessageEng(),
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                "error"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // 데이터베이스 접근 관련 오류 처리
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccessException(DataAccessException ex) {
        Map<String, Object> errorResponse = createErrorResponse(
                "데이터베이스 접근 오류가 발생했습니다. 요청값이 올바른지 확인해주세요.",
                "A database error occurred.",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Database Error",
                "error"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 그 외 모든 예외 처리 (알 수 없는 예외)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        Map<String, Object> errorResponse = createErrorResponse(
                "알 수 없는 오류가 발생했습니다. 서버 빌드 중, 또는 서버/네트워크 오류",
                "An unknown error occurred.",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "error"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // JWT 인증 실패 예외 처리
    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleJwtAuthenticationException(JwtAuthenticationException ex) {
        Map<String, Object> errorResponse = createErrorResponse(
                ex.getMessageKor(),
                ex.getMessageEng(),
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                null
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // 공통 응답 생성 메서드 (한국어 + 영어 메시지 모두 지원)
    private Map<String, Object> createErrorResponse(String messageKor, String messageEng, HttpStatus status, String errorType, String data) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("message_kor", messageKor);
        response.put("message_eng", messageEng);
        response.put("status", status.value());
        response.put("error", errorType);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
