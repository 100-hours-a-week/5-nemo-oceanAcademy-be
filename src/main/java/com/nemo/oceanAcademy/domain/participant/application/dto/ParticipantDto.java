package com.nemo.oceanAcademy.domain.participant.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipantDto {

    //PK 사용자(수강생) 목록 식별자
    private Long id;
    ///FK 사용자(수강생) 아이디
    private Long userId;
    //FK 강의실 아이디
    private Long classroomId;
    //사용자(수강생) 수강 신청 시각
    private LocalDateTime createdAt;
}
