package com.nemo.oceanAcademy.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KakaoConfig {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    public String getKakaoClientId() {
        return kakaoClientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }
}
