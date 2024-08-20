package com.nemo.oceanAcademy.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponseDTO {
    private String accessToken;

    @JsonIgnore  // 클라이언트에 반환하지 않음
    private String refreshToken;

    public static TokenResponseDTO of(String accessToken, String refreshToken) {
        return new TokenResponseDTO(accessToken, refreshToken);
    }
}