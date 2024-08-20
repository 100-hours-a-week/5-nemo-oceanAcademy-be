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

    @NotNull(message = "Nickname is mandatory")
    @Size(min = 2, max = 9, message = "Nickname must be between 2 and 9 characters")
    private String nickname;

    private String email;               // 선택적 필드
    private String profileImagePath;    // 선택적 필드
}
