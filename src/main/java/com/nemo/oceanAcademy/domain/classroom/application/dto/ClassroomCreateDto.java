package com.nemo.oceanAcademy.domain.classroom.application.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassroomCreateDto {

    // FK 카테고리 아이디 (필수)
    @NotNull(message = "카테고리 ID는 필수입니다.")
    @JsonProperty("categoryId")
    private Integer categoryId;

    // FK 사용자(강사) 아이디 (필수)
    @NotNull(message = "사용자(강사) ID는 필수입니다.")
    @JsonProperty("userId")
    private String userId;

    // 강의 이름 (필수)
    @NotNull(message = "강의 이름은 필수입니다.")
    @JsonProperty("name")
    private String name;

    // 강의 목표 (선택)
    @JsonProperty("object")
    private String object;

    // 강의 소개 (선택)
    @JsonProperty("description")
    private String description;

    // 강사 소개 (선택)
    @JsonProperty("instructorInfo")
    private String instructorInfo;

    // 강의 사전지식 및 준비물 (선택)
    @JsonProperty("prerequisite")
    private String prerequisite;

    // 강의 공지 (선택)
    @JsonProperty("announcement")
    private String announcement;

    // 강의 배너 이미지 경로 (선택)
    @JsonProperty("bannerImagePath")
    private String bannerImagePath;

    // 강의실 활성화 여부 (선택)
    @JsonProperty("isActive")
    private Boolean isActive;
}
