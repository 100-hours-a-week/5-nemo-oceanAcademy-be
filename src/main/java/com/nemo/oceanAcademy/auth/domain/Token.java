package com.nemo.oceanAcademy.auth.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token {

    private String userId;
    private String tokenId;

    @Builder
    public Token(String userId) {
        this.userId = userId;
        this.tokenId = UUID.randomUUID().toString();  // 고유한 Token ID 생성
    }
}
