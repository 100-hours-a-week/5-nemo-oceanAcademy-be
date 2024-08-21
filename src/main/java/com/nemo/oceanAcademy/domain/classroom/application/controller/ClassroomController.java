package com.nemo.oceanAcademy.domain.classroom.application.controller;

import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomCreateDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomUpdateDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomResponseDto;
import com.nemo.oceanAcademy.domain.classroom.application.service.ClassroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

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
    public ResponseEntity<ClassroomResponseDto> createClassroom(@RequestBody ClassroomCreateDto classroomCreateDto) {
        ClassroomResponseDto createdClassroom = classroomService.createClassroom(classroomCreateDto);
        return ResponseEntity.status(201).body(createdClassroom); // HTTP 201 Created 반환
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
