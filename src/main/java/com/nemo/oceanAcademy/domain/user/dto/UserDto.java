package com.nemo.oceanAcademy.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    //PK 사용자 아이디, oauth 식별자
    private Long id;
    //사용자 이메일, 중복X, 선택적 등록
    private String email;
    // 사용자 닉네임, 중복X
    private String nickname;
    // 사용자 회원가입 시각
    private LocalDateTime createdAt;
    // 사용자 프로필 사진 경로, 프로필 사진 선택적 등록
    private String profileImagePath;
}
