package com.nemo.oceanAcademy.domain.classroom.application.controller;

import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomCreateDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomUpdateDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomResponseDto;
import com.nemo.oceanAcademy.domain.classroom.application.service.ClassroomService;
import com.nemo.oceanAcademy.domain.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;
    private final JwtTokenProvider jwtTokenProvider;

    // 전체 강의실 조회
    @GetMapping
    public ResponseEntity<List<ClassroomResponseDto>> getAllClassrooms() {
        List<ClassroomResponseDto> classrooms = classroomService.getAllClassrooms();
        return ResponseEntity.ok(classrooms);
    }

    // 개별 강의실 조회
    @GetMapping("/{classId}")
    public ResponseEntity<ClassroomResponseDto> getClassroomById(@PathVariable Long classId) {
        ClassroomResponseDto classroom = classroomService.getClassroomById(classId);
        return ResponseEntity.ok(classroom);
    }

    // 새로운 강의실 생성
    @PostMapping
    public ResponseEntity<ClassroomResponseDto> createClassroom(@RequestHeader("Authorization") String token,
                                                                @RequestBody ClassroomCreateDto classroomCreateDto) {
        String bearerToken = token.replace("Bearer ", "").trim();         // 'Bearer ' 부분을 제거하고 실제 JWT 토큰을 추출
        String userId = jwtTokenProvider.getUserId(bearerToken);                   // JWT 토큰에서 사용자 ID 추출
        classroomCreateDto.setUserId(userId);                                               // 추출한 사용자 ID를 classroomCreateDto에 설정
        System.out.println("userId :" + userId);
        ClassroomResponseDto createdClassroom = classroomService.createClassroom(classroomCreateDto);// 강의실 생성
        System.out.println("wow");
        return ResponseEntity.status(201).body(createdClassroom);                           // HTTP 201 Created 반환
    }

    // 강의실 정보 수정
    @PatchMapping("/{classId}")
    public ResponseEntity<ClassroomResponseDto> updateClassroom(@PathVariable Long classId, @RequestBody ClassroomUpdateDto classroomUpdateDto) {
        ClassroomResponseDto updatedClassroom = classroomService.updateClassroom(classId, classroomUpdateDto);
        return ResponseEntity.ok(updatedClassroom);
    }

    // 강의실 삭제
    @DeleteMapping("/{classId}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long classId) {
        classroomService.deleteClassroom(classId);
        return ResponseEntity.noContent().build(); // HTTP 204 No Content 반환
    }
}
