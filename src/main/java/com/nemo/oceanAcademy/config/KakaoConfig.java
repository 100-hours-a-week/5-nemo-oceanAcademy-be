package com.nemo.oceanAcademy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KakaoConfig {

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    public String getKakaoClientId() {
        return kakaoClientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }
}
