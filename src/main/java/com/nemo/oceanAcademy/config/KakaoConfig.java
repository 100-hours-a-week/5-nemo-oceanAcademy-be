package com.nemo.oceanAcademy.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KakaoConfig {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-local-uri}")
    private String redirectLocalUri;

    @Value("${kakao.redirect-server-uri}")
    private String redirectServerUri;

    @Value("${kakao.redirect-dev-uri}")
    private String redirectDevUri;

    public String getKakaoClientId() {
        return kakaoClientId;
    }

    public String getLocalRedirectUri() { return redirectLocalUri; }

    public String getServerRedirectUri() {
        return redirectServerUri;
    }

    public String getDevRedirectUri() { return redirectDevUri; }
}

