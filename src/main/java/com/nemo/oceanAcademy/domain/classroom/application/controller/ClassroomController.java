package com.nemo.oceanAcademy.domain.classroom.application.controller;

import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomCreateDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomUpdateDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomResponseDto;
import com.nemo.oceanAcademy.domain.classroom.application.service.ClassroomService;
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

    // 전체 강의실 조회 - 인증 불필요
    @GetMapping
    public ResponseEntity<List<ClassroomResponseDto>> getAllClassrooms() {
        List<ClassroomResponseDto> classrooms = classroomService.getAllClassrooms();
        System.out.println("모든 강의실 조회 완료!");
        return ResponseEntity.ok(classrooms);
    }

    // 개별 강의실 조회
    @GetMapping("/{classId}")
    public ResponseEntity<ClassroomResponseDto> getClassroomById(@PathVariable Long classId) {
        ClassroomResponseDto classroom = classroomService.getClassroomById(classId);
        System.out.println("개별 강의실 조회 완료!");
        return ResponseEntity.ok(classroom);
    }

    // 새로운 강의실 생성
    @PostMapping
    public ResponseEntity<ClassroomResponseDto> createClassroom(HttpServletRequest request, @RequestBody ClassroomCreateDto classroomCreateDto) {
        // 인증: 사용자 ID - From JwtAuthenticationFilter
        String userId = (String) request.getAttribute("userId");
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

    // 강의실 정보 업데이트
    @PatchMapping("/{classId}")
    public ResponseEntity<ClassroomResponseDto> updateClassroom(@PathVariable Long classId, @RequestBody ClassroomUpdateDto classroomUpdateDto) {
        ClassroomResponseDto updatedClassroom = classroomService.updateClassroom(classId, classroomUpdateDto);
        System.out.println("강의실 정보 업데이트 완료!");
        return ResponseEntity.ok(updatedClassroom);
    }

    // 강의실 삭제 - soft delete - 강사만 가능
    @DeleteMapping("/{classId}")
    public ResponseEntity<Void> deleteClassroom(HttpServletRequest request, @PathVariable Long classId) {
        // 인증: 사용자 ID - From JwtAuthenticationFilter
        String userId = (String) request.getAttribute("userId");
        if (userId == null) {
            System.out.println("userId 추출 불가, 인증되지 않은 요청");
            return ResponseEntity.status(401).body(null);
        }
        // 강의실 삭제 로직
        classroomService.deleteClassroom(classId);
        System.out.println("강의실 삭제 완료!");
        return ResponseEntity.noContent().build();  // 204 No Content
    }
}
