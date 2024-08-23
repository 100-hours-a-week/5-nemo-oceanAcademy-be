package com.nemo.oceanAcademy.domain.classroom.application.controller;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomCreateDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomDashboardDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomUpdateDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomResponseDto;
import com.nemo.oceanAcademy.domain.classroom.application.service.ClassroomService;
import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
/*
    /api/classes
        Get - 전체 강의실 조회
        Post - 새로운 강의실 생성

    /api/classes/{classId}/role
        Get - 해당 강의실의 "강사/수강생/관계없음" 구분

    /api/classes/{classId}
        Get - 개별 강의실 조회
        Patch - 강의실 정보 업데이트

    /api/classes/{classId}/delete - 숙희분과 상의함, api 변경
        Patch - 강의실 삭제

    /api/classes/{classId}/dashboard
        Get - 강의 대시보드 정보 불러오기

    /{classId}/dashboard/students
        Get - 강의를 듣는 수강생 리스트 정보 불러오기
*/
@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    // TODO : 전체 강의실 조회 - 성공
    @GetMapping
    public ResponseEntity<List<ClassroomResponseDto>> getAllClassrooms(
            @RequestParam(value = "target", required = false) String target,
            @RequestParam(value = "category", required = false) Integer categoryId,
            @RequestParam(value = "page", defaultValue = "0") int page) {

        // 한 페이지에 10개씩 표시
        int pageSize = 10;

        // 서비스 메서드 호출 및 필터링/페이징 처리
        List<ClassroomResponseDto> classrooms = classroomService.getFilteredClassrooms(target, categoryId, page, pageSize);

        System.out.println("필터링된 강의실 조회 완료!");
        return ResponseEntity.ok(classrooms);
    }


    // TODO : 새로운 강의실 생성
    // “/role api 해당 강의실의 "강사/수강생/관계없음" 구분”
    @PostMapping
    public ResponseEntity<ClassroomResponseDto> createClassroom(HttpServletRequest request, @RequestBody ClassroomCreateDto classroomCreateDto) {
        // 인증: 사용자 ID - From JwtAuthenticationFilter
        String userId = (String) request.getAttribute("userId");

        System.out.println(request);
        System.out.println(classroomCreateDto);
        if (userId == null) {
            System.out.println("userId 추출 불가, 인증되지 않은 요청");
            return ResponseEntity.status(401).body(null);
        }
        // 강의실 생성 로직
        classroomCreateDto.setUserId(userId);
        ClassroomResponseDto createdClassroom = classroomService.createClassroom(classroomCreateDto);  // 강의실 생성
        System.out.println("새로운 강의실 생성 완료!");
        return ResponseEntity.status(201).body(createdClassroom);  // 201 Created
    }

    // TODO : 해당 강의실의 "강사/수강생/관계없음" 구분 - 성공 , 수강생만 확인
    @GetMapping("/{classId}/role")
    public ResponseEntity<String> getUserRoleInClassroom(HttpServletRequest request, @PathVariable Long classId) {
        String userId = (String) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("인증되지 않은 요청");
        }

        String role = classroomService.getUserRoleInClassroom(classId, userId);
        return ResponseEntity.ok(role);
    }


    // TODO : 개별 강의실 조회 - 성공
    // “/role api 해당 강의실의 "강사/수강생/관계없음" 구분”
    @GetMapping("/{classId}")
    public ResponseEntity<ClassroomResponseDto> getClassroomById(@PathVariable Long classId) {
        ClassroomResponseDto classroom = classroomService.getClassroomById(classId);
        System.out.println("개별 강의실 조회 완료!");
        return ResponseEntity.ok(classroom);
    }


    // TODO : 강의실 정보 업데이트
    // “/role api 해당 강의실의 "강사/수강생/관계없음" 구분”
    @PatchMapping("/{classId}")
    public ResponseEntity<ClassroomResponseDto> updateClassroom(@PathVariable Long classId, @RequestBody ClassroomUpdateDto classroomUpdateDto) {
        ClassroomResponseDto updatedClassroom = classroomService.updateClassroom(classId, classroomUpdateDto);
        System.out.println("강의실 정보 업데이트 완료!");
        return ResponseEntity.ok(updatedClassroom);
    }


    // TODO : 강의실 삭제 - soft delete - 강사만 가능
    // “/role api 해당 강의실의 "강사/수강생/관계없음" 구분”
    @PatchMapping("/{classId}/delete")
    public ResponseEntity<Void> deleteClassroom(HttpServletRequest request, @PathVariable Long classId) {
        // 인증: 사용자 ID - From JwtAuthenticationFilter
        String userId = (String) request.getAttribute("classId");
        if (userId == null) {
            System.out.println("userId 추출 불가, 인증되지 않은 요청");
            return ResponseEntity.status(401).body(null);
        }
        // 강의실 삭제 로직
        classroomService.deleteClassroom(classId);
        System.out.println("강의실 삭제 완료!");
        return ResponseEntity.noContent().build();  // 204 No Content
    }


    // TODO : 강의 대시보드 정보와 스케줄 정보 불라오기 - 성공
    // “/role api 해당 강의실의 "강사/수강생/관계없음" 구분”
    @GetMapping("/{classId}/dashboard")
    public ResponseEntity<ClassroomDashboardDto> getClassroomDashboard(@PathVariable Long classId, HttpServletRequest request) {

        // 인증: 사용자 ID - From JwtAuthenticationFilter
        String userId = (String) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(null);
        }

        // 대시보드 정보와 스케줄 정보 함께 조회
        ClassroomDashboardDto dashboard = classroomService.getClassroomDashboard(classId, userId);

        return ResponseEntity.ok(dashboard);
    }

    // TODO : 강의를 듣는 수강생 리스트 정보 조회 - 성공
    @GetMapping("/{classId}/dashboard/students")
    public ResponseEntity<List<User>> getClassroomStudents(@PathVariable Long classId) {
        List<User> students = classroomService.getClassroomStudents(classId);
        return ResponseEntity.ok(students);
    }

    // TODO : /api/classes/{classId}/enroll 수강신청 개발
}
