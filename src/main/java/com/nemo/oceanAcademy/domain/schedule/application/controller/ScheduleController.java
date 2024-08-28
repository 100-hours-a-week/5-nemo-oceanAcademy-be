package com.nemo.oceanAcademy.domain.schedule.application.controller;
import com.nemo.oceanAcademy.common.exception.RoleUnauthorizedException;
import com.nemo.oceanAcademy.common.exception.UnauthorizedException;
import com.nemo.oceanAcademy.common.response.ApiResponse;
import com.nemo.oceanAcademy.domain.classroom.application.service.ClassroomService;
import com.nemo.oceanAcademy.domain.schedule.application.dto.ScheduleDto;
import com.nemo.oceanAcademy.domain.schedule.application.service.ScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ScheduleController는 강의 일정 관련 API를 처리합니다.
 * 강의 일정 조회, 생성, 삭제 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/classes/{classId}/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
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
     * 강의 일정 목록 조회
     * @param classId 강의 ID
     * @param request 인증된 사용자 정보 포함 요청 객체
     * @return ResponseEntity<Map<String, Object>> 강의 일정 목록
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getSchedulesByClassId(HttpServletRequest request, @PathVariable Long classId) {
        String userId = getAuthenticatedUserId(request);
        String role = classroomService.getUserRoleInClassroom(classId, userId);
        System.out.println("role: " + role);

        // 수강생이나 강사만 접근 가능
        if (role.equals("관계없음")) {
            throw new RoleUnauthorizedException("해당 강의에 접근 권한이 없습니다.", "Access denied");
        }

        List<ScheduleDto> schedules = scheduleService.getSchedulesByClassId(classId, userId);
        return ApiResponse.success("강의 일정 목록 조회 성공", "Schedules retrieved successfully", schedules);
    }

    /**
     * 강의 일정 생성
     * @param classId 강의 ID
     * @param scheduleDto 생성할 강의 일정 정보
     * @param request 인증된 사용자 정보 포함 요청 객체
     * @return ResponseEntity<Map<String, Object>> 생성된 강의 일정 정보
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createSchedule(HttpServletRequest request, @PathVariable Long classId, @RequestBody ScheduleDto scheduleDto) {
        String userId = getAuthenticatedUserId(request);
        String role = classroomService.getUserRoleInClassroom(classId, userId);
        System.out.println("role: " + role);

        // 강사만 접근 가능
        if (!role.equals("강사")) {
            throw new RoleUnauthorizedException("해당 강의 일정을 생성할 권한이 없습니다.", "Access denied");
        }
        ScheduleDto createdSchedule = scheduleService.createSchedule(classId, scheduleDto, userId);
        return ApiResponse.success("강의 일정 생성 성공", "Schedule created successfully", createdSchedule);
    }

    /**
     * 강의 일정 삭제
     * @param classId 강의 ID
     * @param scheduleData 삭제할 일정 ID
     * @param request 인증된 사용자 정보 포함 요청 객체
     * @return ResponseEntity<Map<String, Object>> 삭제 결과
     */
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteSchedule(HttpServletRequest request,
                                                              @PathVariable Long classId,
                                                              @RequestBody Map<String, Long> scheduleData) {
        String userId = getAuthenticatedUserId(request);
        String role = classroomService.getUserRoleInClassroom(classId, userId);
        System.out.println("role: " + role);

        // 강사만 접근 가능
        if (!role.equals("강사")) {
            throw new RoleUnauthorizedException("해당 강의 일정을 삭제할 권한이 없습니다.", "Access denied");
        }
        Long scheduleIndex = scheduleData.get("schedule_id");
        scheduleService.deleteSchedule(classId, scheduleIndex, userId);
        return ApiResponse.success("강의 일정 삭제 성공", "Schedule deleted successfully", null);
    }
}
