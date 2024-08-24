package com.nemo.oceanAcademy.domain.schedule.application.controller;
import com.nemo.oceanAcademy.domain.schedule.application.dto.ScheduleDto;
import com.nemo.oceanAcademy.domain.schedule.application.service.ScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/*
    /api/classes/{classId}/schedule
        Get - 강의 일정 불러오기
        Post - 강의 일정 생성하기
    /api/classes/{classId}/schedule/{id}
        Delete - 강의 일정 삭제하기

    “/role api 해당 강의실의 "강사/수강생/관계없음" 구분”
        /api/classes/{classId}/role
*/

@RestController
@RequestMapping("/api/classes/{classId}/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // TODO : 강의 일정 불러오기 - 성공
    @GetMapping
    public ResponseEntity<List<ScheduleDto>> getSchedulesByClassId(@PathVariable Long classId, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");  // JWT에서 추출한 사용자 ID 가져오기
        System.out.println(userId);
        List<ScheduleDto> schedules = scheduleService.getSchedulesByClassId(classId, userId);
        return ResponseEntity.ok(schedules);
    }

    // TODO : 강의 일정 생성하기 - 성공
    @PostMapping
    public ResponseEntity<ScheduleDto> createSchedule(@PathVariable Long classId, @RequestBody ScheduleDto scheduleDto, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");  // JWT에서 추출한 사용자 ID 가져오기
        System.out.println(userId);
        ScheduleDto createdSchedule = scheduleService.createSchedule(classId, scheduleDto, userId);
        return ResponseEntity.ok(createdSchedule);
    }

    // TODO : 강의 일정 삭제하기 - 성공
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long classId, @PathVariable Long id, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");  // JWT에서 추출한 사용자 ID 가져오기
        System.out.println(userId);
        scheduleService.deleteSchedule(classId, id, userId);
        return ResponseEntity.noContent().build();
    }
}
