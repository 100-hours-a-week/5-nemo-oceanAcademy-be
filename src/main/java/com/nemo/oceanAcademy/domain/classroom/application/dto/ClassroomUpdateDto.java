package com.nemo.oceanAcademy.domain.classroom.application.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassroomUpdateDto {

    // 강의 이름 (필수, 수정 가능한 필드)
    @Column(nullable = true)
    @JsonProperty("name")
    private String name;

    // 강의 목표 (선택)
    @Column(nullable = true)
    @JsonProperty("object")
    private String object;

    // 강의 소개 (선택)
    @Column(nullable = true)
    @JsonProperty("description")
    private String description;

    // 강사 소개 (선택)
    @Column(nullable = true)
    @JsonProperty("instructorInfo")
    private String instructorInfo;

    // 강의 사전지식 및 준비물 (선택)
    @Column(nullable = true)
    @JsonProperty("prerequisite")
    private String prerequisite;

    // 강의 공지 (선택)
    @Column(nullable = true)
    @JsonProperty("announcement")
    private String announcement;

    // 강의 배너 이미지 경로 (선택)
    @Column(nullable = true)
    @JsonProperty("bannerImagePath")
    private String bannerImagePath;

    // 강의실 활성화 여부 (선택)
    @Column(nullable = true)
    @JsonProperty("isActive")
    private Boolean isActive;
}
