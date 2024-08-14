package com.nemo.oceanAcademy.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthDto {
    private Long oauthId;
    private String nickname;
    private String email;
    private String profileImagePath;
}
