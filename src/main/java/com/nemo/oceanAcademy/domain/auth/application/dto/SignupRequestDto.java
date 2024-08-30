package com.nemo.oceanAcademy.domain.auth.application.dto;

import lombok.Data;

@Data
public class SignupRequestDto {
    private String nickname;
    private String email;
}
