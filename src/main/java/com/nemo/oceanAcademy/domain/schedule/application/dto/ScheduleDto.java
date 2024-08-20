package com.nemo.oceanAcademy.domain.schedule.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleDto {

    // PK 일정 아이디 식별자
    private Long id;
    // FK 강의실 아이디
    private Long classId;
    // 강의 일정 내용
    private String content;
    // 강의 일정 날짜, 수동 설정
    private LocalDate date;
    // 강의 시작 시각, 수동 설정
    private LocalTime startTime;
    // 강의 종료 시각, 수동 설정
    private LocalTime finishTime;
}
