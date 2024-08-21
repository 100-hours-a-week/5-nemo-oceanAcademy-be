package com.nemo.oceanAcademy.domain.classroom.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassroomUpdateDto {

    // 강의 이름 (필수, 수정 가능한 필드)
    @NotNull(message = "강의 이름은 필수입니다.")
    private String name;

    // 강의 목표 (선택)
    private String object;

    // 강의 소개 (선택)
    private String description;

    // 강사 소개 (선택)
    private String instructorInfo;

    // 강의 사전지식 및 준비물 (선택)
    private String prerequisite;

    // 강의 공지 (선택)
    private String announcement;

    // 강의 배너 이미지 경로 (선택)
    private String bannerImagePath;

    // 강의실 활성화 여부 (선택)
    private Boolean isActive;
}
