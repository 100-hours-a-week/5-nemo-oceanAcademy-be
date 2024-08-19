package com.nemo.oceanAcademy.domain.classroom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassroomDto {

    //PK 강의실 아이디, 식별자
    private Long id;
    //FK 사용자(강사) 아이디
    private Long userId;
    //FK 카테고리 아이디
    private Long categoryId;
    //강의 이름
    private String name;
    //강의 목표
    private String object;
    //강의 소개
    private String description;
    //강사 소개, 선택적 작성
    private String instructorInfo;
    //강의 사전지식 및 준비물, 선택적 작성
    private String prerequisite;
    //강의실 공지
    private String announcement;
    //강의실 배너 사진 경로, 선택적 작성
    private String bannerImagePath;
    //강의실 라이브 강의 활성화 여부
    private Boolean isActive;
}
