package com.nemo.oceanAcademy.domain.schedule.application.controller;

import com.nemo.oceanAcademy.domain.schedule.application.dto.ScheduleDto;
import com.nemo.oceanAcademy.domain.schedule.application.service.ScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes/{classId}/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 특정 강의실에 속한 모든 스케줄 조회
    @GetMapping
    public ResponseEntity<List<ScheduleDto>> getSchedulesByClassId(@PathVariable Long classId, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");  // JWT에서 추출한 사용자 ID 가져오기
        System.out.println(userId);
        List<ScheduleDto> schedules = scheduleService.getSchedulesByClassId(classId, userId);
        return ResponseEntity.ok(schedules);
    }

    // 특정 스케줄 조회
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDto> getScheduleById(@PathVariable Long classId, @PathVariable Long id, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");  // JWT에서 추출한 사용자 ID 가져오기
        System.out.println(userId);
        ScheduleDto scheduleDto = scheduleService.getScheduleByIdAndClassId(classId, id, userId);
        return ResponseEntity.ok(scheduleDto);
    }

    // 스케줄 생성
    @PostMapping
    public ResponseEntity<ScheduleDto> createSchedule(@PathVariable Long classId, @RequestBody ScheduleDto scheduleDto, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");  // JWT에서 추출한 사용자 ID 가져오기
        System.out.println(userId);
        ScheduleDto createdSchedule = scheduleService.createSchedule(classId, scheduleDto, userId);
        return ResponseEntity.ok(createdSchedule);
    }

    // 스케줄 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long classId, @PathVariable Long id, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");  // JWT에서 추출한 사용자 ID 가져오기
        System.out.println(userId);
        scheduleService.deleteSchedule(classId, id, userId);
        return ResponseEntity.noContent().build();
    }
}
