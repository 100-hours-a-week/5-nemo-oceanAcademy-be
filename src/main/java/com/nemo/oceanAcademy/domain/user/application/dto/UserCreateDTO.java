package com.nemo.oceanAcademy.domain.user.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {

    private String userId;

    @NotNull(message = "Nickname is mandatory")
    @Size(min = 2, max = 9, message = "Nickname must be between 2 and 9 characters")
    private String nickname;

    // 프로필 이미지 경로 (선택적 필드)
    private String profileImagePath;

    // 이메일 (선택적 필드)
    private String email;
}
