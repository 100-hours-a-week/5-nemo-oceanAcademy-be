package com.nemo.oceanAcademy.domain.classroom.application.controller.v2;

import com.nemo.oceanAcademy.common.exception.UnauthorizedException;
import com.nemo.oceanAcademy.common.response.ApiResponse;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomResponseDto;
import com.nemo.oceanAcademy.domain.classroom.application.service.ClassroomService;
import io.sentry.Sentry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 강의 리뷰 로직 변경에 따른 부하를 확인합니다.
 * 테스트용 API입니다.
 */

@RestController
@RequestMapping("/api/v2/classes")
@RequiredArgsConstructor
public class ClassroomControllerV2 {

    private final ClassroomService classroomService;

    private String getAuthenticatedUserId(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        if (userId == null) {
            UnauthorizedException exception = new UnauthorizedException(
                    "사용자 인증에 실패했습니다. (토큰 없음)",
                    "Unauthorized request: userId not found"
            );
            Sentry.captureException(exception);
            throw exception;
        }
        return userId;
    }

    // 강의 리스트 GET '/'
    @GetMapping
    public ResponseEntity<?> getAllClassrooms(
            HttpServletRequest request,
            @RequestParam(value = "target", required = false) String target,
            @RequestParam(value = "category", required = false) Integer categoryId,
            @RequestParam(value = "page", defaultValue = "0") int page) {

        String userId = null;
        if ("enrolled".equals(target) || "created".equals(target)) {
            userId = getAuthenticatedUserId(request);
        }

        List<ClassroomResponseDto> classrooms = classroomService.getFilteredClassrooms(target, categoryId, userId, page, 10);
        return ApiResponse.success("강의실 목록 조회 성공", "Classrooms retrieved successfully", classrooms);
    }

    // 강의 정보 GET '/{classId}'
    @GetMapping("/{classId}")
    public ResponseEntity<?> getClassroomById(@PathVariable Long classId) {
        ClassroomResponseDto classroom = classroomService.getClassroomById(classId);
        return ApiResponse.success("강의실 조회 성공", "Classroom retrieved successfully", classroom);
    }

}
