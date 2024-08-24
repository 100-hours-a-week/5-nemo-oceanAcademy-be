package com.nemo.oceanAcademy.domain.classroom.application.dto;
import com.nemo.oceanAcademy.domain.schedule.application.dto.ScheduleDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassroomDashboardDto {

    private Long id;                  // PK 강의실 아이디
    private String userId;            // 강사 아이디
    private Integer categoryId;           // 카테고리 아이디
    private String name;              // 강의 이름
    private String object;            // 강의 목표
    private String description;       // 강의 소개
    private String instructorInfo;    // 강사 소개
    private String prerequisite;      // 사전 지식 및 준비 안내
    private String announcement;      // 강의 공지
    private String bannerImagePath;   // 배너 이미지 경로
    private Boolean isActive;         // 강의실 활성화 여부
    private String role;              // 사용자 역할 (강사, 수강생, 관계없음)
    private List<ScheduleDto> schedules;  // 스케줄 목록
}
