package com.nemo.oceanAcademy.domain.user.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    private String id;

    private String nickname;            // 선택적 필드
    private String email;               // 선택적 필드
    private String profileImagePath;    // 선택적 필드
}
