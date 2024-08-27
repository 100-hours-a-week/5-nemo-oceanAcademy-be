package com.nemo.oceanAcademy.domain.classroom.application.controller;
import com.nemo.oceanAcademy.common.exception.UnauthorizedException;
import com.nemo.oceanAcademy.common.response.ApiResponse;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomCreateDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomDashboardDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomUpdateDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomResponseDto;
import com.nemo.oceanAcademy.domain.classroom.application.exception.ClassroomNotFoundException;
import com.nemo.oceanAcademy.domain.classroom.application.service.ClassroomService;
import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    // 공통 사용자 인증 처리 메서드
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

    /*
     * GET /api/classes
     * 전체 강의실 조회
     *
     * 요청(Request):
     * - Query parameters:
     *   - target (선택, String): 필터링 옵션 (예: "enrolled", "created")
     *   - category (선택, Integer): 카테고리 ID
     *   - page (선택, 기본값 0, Integer): 페이지 번호
     *
     * 응답(Response):
     * - 응답 데이터 타입: List<ClassroomResponseDto>
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

    /*
     * POST /api/classes
     * 새로운 강의실 생성
     *
     * 요청(Request):
     * - Body: ClassroomCreateDto (강의실 생성에 필요한 데이터)
     *
     * 응답(Response):
     * - 응답 데이터 타입: ClassroomResponseDto (생성된 강의실 정보)
     */
    @PostMapping
    public ResponseEntity<?> createClassroom(HttpServletRequest request, @RequestBody ClassroomCreateDto classroomCreateDto) {
        String userId = getAuthenticatedUserId(request);
        classroomCreateDto.setUserId(userId);
        ClassroomResponseDto createdClassroom = classroomService.createClassroom(classroomCreateDto);
        return ApiResponse.success("강의실 생성 성공", "Classroom created successfully", createdClassroom);
    }

    /*
     * GET /api/classes/{classId}/role
     * 해당 강의실에서의 사용자 역할 확인
     *
     * 요청(Request):
     * - Path Variable: classId (Long, 강의실 ID)
     *
     * 응답(Response):
     * - 응답 데이터 타입: String (강사/수강생/관계없음)
     */
    @GetMapping("/{classId}/role")
    public ResponseEntity<?> getUserRoleInClassroom(HttpServletRequest request, @PathVariable Long classId) {
        String userId = getAuthenticatedUserId(request);
        String role = classroomService.getUserRoleInClassroom(classId, userId);
        return ApiResponse.success("역할 조회 성공", "Role retrieved successfully", role);
    }

    /*
     * GET /api/classes/{classId}
     * 개별 강의실 정보 조회
     *
     * 요청(Request):
     * - Path Variable: classId (Long, 강의실 ID)
     *
     * 응답(Response):
     * - 응답 데이터 타입: ClassroomResponseDto (강의실 정보)
     */
    @GetMapping("/{classId}")
    public ResponseEntity<?> getClassroomById(@PathVariable Long classId) {
        ClassroomResponseDto classroom = classroomService.getClassroomById(classId);
        if (classroom == null) {
            throw new ClassroomNotFoundException(
                    "강의실을 찾을 수 없습니다.",
                    "Classroom with ID " + classId + " not found"
            );
        }
        return ApiResponse.success("강의실 조회 성공", "Classroom retrieved successfully", classroom);
    }

    /*
     * PATCH /api/classes/{classId}
     * 강의실 정보 업데이트
     *
     * 요청(Request):
     * - Path Variable: classId (Long, 강의실 ID)
     * - Body: ClassroomUpdateDto (업데이트할 데이터)
     *
     * 응답(Response):
     * - 응답 데이터 타입: ClassroomResponseDto (업데이트된 강의실 정보)
     */
    @PatchMapping("/{classId}")
    public ResponseEntity<?> updateClassroom(HttpServletRequest request, @PathVariable Long classId, @RequestBody ClassroomUpdateDto classroomUpdateDto) {
        String userId = getAuthenticatedUserId(request);
        ClassroomResponseDto updatedClassroom = classroomService.updateClassroom(classId, classroomUpdateDto);
        return ApiResponse.success("강의실 정보 업데이트 성공", "Classroom updated successfully", updatedClassroom);
    }

    /*
     * DELETE /api/classes/{classId}/delete
     * 강의실 삭제 (soft delete)
     *
     * 요청(Request):
     * - Path Variable: classId (Long, 강의실 ID)
     *
     * 응답(Response):
     * - 응답 데이터 없음 (204 No Content)
     */
    @DeleteMapping("/{classId}/delete")
    public ResponseEntity<?> deleteClassroom(HttpServletRequest request, @PathVariable Long classId) {
        String userId = getAuthenticatedUserId(request);
        classroomService.deleteClassroom(classId);
        // return ResponseEntity.noContent().build(); // 응답 없음, No Content
        return ApiResponse.success("강의실 삭제 성공", "Classroom deleted successfully", null);
    }

    /*
     * GET /api/classes/{classId}/dashboard
     * 강의 대시보드 정보 불러오기
     *
     * 요청(Request):
     * - Path Variable: classId (Long, 강의실 ID)
     *
     * 응답(Response):
     * - 응답 데이터 타입: ClassroomDashboardDto (대시보드 정보)
     */
    @GetMapping("/{classId}/dashboard")
    public ResponseEntity<?> getClassroomDashboard(@PathVariable Long classId, HttpServletRequest request) {
        String userId = getAuthenticatedUserId(request);
        ClassroomDashboardDto dashboard = classroomService.getClassroomDashboard(classId, userId);
        return ApiResponse.success("대시보드 조회 성공", "Dashboard retrieved successfully", dashboard);
    }

    /*
     * GET /api/classes/{classId}/dashboard/students
     * 강의를 듣는 수강생 리스트 조회
     *
     * 요청(Request):
     * - Path Variable: classId (Long, 강의실 ID)
     *
     * 응답(Response):
     * - 응답 데이터 타입: List<User> (수강생 리스트)
     */
    @GetMapping("/{classId}/dashboard/students")
    public ResponseEntity<?> getClassroomStudents(HttpServletRequest request, @PathVariable Long classId) {
        String userId = getAuthenticatedUserId(request);
        List<User> students = classroomService.getClassroomStudents(classId);
        return ApiResponse.success("수강생 목록 조회 성공", "Students retrieved successfully", students);
    }

    /*
     * POST /api/classes/{classId}/enroll
     * 수강 신청
     *
     * 요청(Request):
     * - Path Variable: classId (Long, 강의실 ID)
     *
     * 응답(Response):
     * - 응답 데이터 타입: 응답 데이터 없음 (200 OK)
     */
    @PostMapping("/{classId}/enroll")
    public ResponseEntity<?> enrollParticipant(HttpServletRequest request, @PathVariable Long classId) {
        String userId = getAuthenticatedUserId(request);
        classroomService.enrollParticipant(userId, classId);
        return ApiResponse.success("수강 신청 완료", "Enrollment successful", null);
    }
}
