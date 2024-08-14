package com.nemo.oceanAcademy.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDto {

    //PK 리뷰 아이디, 식별자
    private Long id;
    //FK 강의실 아이디
    private Long classId;
    //FK 사용자(수강자) 아이디
    private Long userId;
    //강의 별점
    private Float rating;
    //리뷰 생성 시각
    private LocalDateTime createdAt;
    //리뷰 내용
    private String content;
}
