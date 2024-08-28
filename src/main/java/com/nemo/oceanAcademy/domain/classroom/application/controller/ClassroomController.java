package com.nemo.oceanAcademy.domain.classroom.application.controller;
import com.nemo.oceanAcademy.common.exception.UnauthorizedException;
import com.nemo.oceanAcademy.common.response.ApiResponse;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomCreateDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomDashboardDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomUpdateDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomResponseDto;
import com.nemo.oceanAcademy.domain.classroom.application.service.ClassroomService;
import com.nemo.oceanAcademy.domain.participant.application.dto.ParticipantResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * ClassroomController는 강의 관련 메인 API를 처리합니다.
 * 강의 조회, 생성, 삭제 기능 등을 제공합니다.
 */
@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    /**
     * 공통 사용자 인증 처리 메서드 - request에서 userId를 추출해 인증된 사용자 ID를 반환
     * @param request HttpServletRequest 객체로부터 userId 추출
     * @return userId 인증된 사용자 ID
     * @throws UnauthorizedException 사용자 ID가 없을 경우 예외 발생
     */
    private String getAuthenticatedUserId(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        if (userId == null) {
            throw new UnauthorizedException(
                    "사용자 인증에 실패했습니다. (토큰 없음)",
                    "Unauthorized request: userId not found"
            );
        }
        return userId;
    }

    /**
     * 전체 강의실 목록 조회
     * @param request    인증된 사용자 요청 객체
     * @param target     필터링 옵션 ("enrolled", "created" 등)
     * @param categoryId 카테고리 ID (선택)
     * @param page       페이지 번호 (기본값: 0)
     * @return ResponseEntity<List<ClassroomResponseDto>> 강의실 목록
     */
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

    /**
     * 새로운 강의실 생성
     * @param request            인증된 사용자 요청 객체
     * @param classroomCreateDto 생성할 강의실 정보
     * @return ResponseEntity<ClassroomResponseDto> 생성된 강의실 정보
     */
    @PostMapping
    public ResponseEntity<?> createClassroom(HttpServletRequest request, @Valid @RequestBody ClassroomCreateDto classroomCreateDto) {
        String userId = getAuthenticatedUserId(request);
        classroomCreateDto.setUserId(userId);
        ClassroomResponseDto createdClassroom = classroomService.createClassroom(classroomCreateDto);
        return ApiResponse.success("강의실 생성 성공", "Classroom created successfully", createdClassroom);
    }

    /**
     * 사용자 역할 확인
     * @param request 인증된 사용자 요청 객체
     * @param classId 강의실 ID
     * @return ResponseEntity<String> 강사/수강생/관계없음의 사용자 역할
     */
    @GetMapping("/{classId}/role")
    public ResponseEntity<?> getUserRoleInClassroom(HttpServletRequest request, @PathVariable Long classId) {
        String userId = getAuthenticatedUserId(request);
        String role = classroomService.getUserRoleInClassroom(classId, userId);
        return ApiResponse.success("역할 조회 성공", "Role retrieved successfully", role);
    }

    /**
     * 강의실 개별 정보 조회
     * @param classId 강의실 ID
     * @return ResponseEntity<ClassroomResponseDto> 강의실 정보
     */
    @GetMapping("/{classId}")
    public ResponseEntity<?> getClassroomById(@PathVariable Long classId) {
        ClassroomResponseDto classroom = classroomService.getClassroomById(classId);
        return ApiResponse.success("강의실 조회 성공", "Classroom retrieved successfully", classroom);
    }

    /**
     * 강의실 정보 업데이트
     * @param request            인증된 사용자 요청 객체
     * @param classId            강의실 ID
     * @param classroomUpdateDto 업데이트할 정보
     * @return ResponseEntity<ClassroomResponseDto> 업데이트된 강의실 정보
     */
    @PatchMapping("/{classId}")
    public ResponseEntity<?> updateClassroom(HttpServletRequest request, @PathVariable Long classId, @Valid @RequestBody ClassroomUpdateDto classroomUpdateDto) {
        String userId = getAuthenticatedUserId(request);
        ClassroomResponseDto updatedClassroom = classroomService.updateClassroom(classId, classroomUpdateDto);
        return ApiResponse.success("강의실 정보 업데이트 성공", "Classroom updated successfully", updatedClassroom);
    }

    /**
     * 강의실 삭제 (Soft Delete)
     * @param request 인증된 사용자 요청 객체
     * @param classId 강의실 ID
     * @return ResponseEntity<?> 삭제 완료 메시지 (204 No Content)
     */
    @DeleteMapping("/{classId}/delete")
    public ResponseEntity<?> deleteClassroom(HttpServletRequest request, @PathVariable Long classId) {
        String userId = getAuthenticatedUserId(request);
        classroomService.deleteClassroom(classId);
        return ApiResponse.success("강의실 삭제 성공", "Classroom deleted successfully", null);
    }

    /**
     * 강의 대시보드 정보 조회
     * @param request 인증된 사용자 요청 객체
     * @param classId 강의실 ID
     * @return ResponseEntity<ClassroomDashboardDto> 강의 대시보드 정보
     */
    @GetMapping("/{classId}/dashboard")
    public ResponseEntity<?> getClassroomDashboard(HttpServletRequest request, @PathVariable Long classId) {
        String userId = getAuthenticatedUserId(request);
        ClassroomDashboardDto dashboard = classroomService.getClassroomDashboard(classId, userId);
        return ApiResponse.success("대시보드 조회 성공", "Dashboard retrieved successfully", dashboard);
    }

    /**
     * 강의를 듣는 수강생 리스트 조회
     * @param request 인증된 사용자 요청 객체
     * @param classId 강의실 ID
     * @return ResponseEntity<List<User>> 수강생 리스트
     */
    @GetMapping("/{classId}/dashboard/students")
    public ResponseEntity<?> getClassroomStudents(HttpServletRequest request, @PathVariable Long classId) {
        String userId = getAuthenticatedUserId(request);

        // 수강생 리스트를 가져와서 DTO로 변환 후 반환
        List<ParticipantResponseDto> students = classroomService.getClassroomStudents(classId);
        return ApiResponse.success("수강생 목록 조회 성공", "Students retrieved successfully", students);
    }

    /**
     * 수강 신청
     * @param request 인증된 사용자 요청 객체
     * @param classId 강의실 ID
     * @return ResponseEntity<?> 수강 신청 성공 메시지
     */
    @PostMapping("/{classId}/enroll")
    public ResponseEntity<?> enrollParticipant(HttpServletRequest request, @PathVariable Long classId) {
        String userId = getAuthenticatedUserId(request);
        classroomService.enrollParticipant(userId, classId);
        return ApiResponse.success("수강 신청 완료", "Enrollment successful", null);
    }
}
