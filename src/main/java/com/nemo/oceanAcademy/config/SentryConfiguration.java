package com.nemo.oceanAcademy.config;

import io.sentry.Sentry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
public class SentryConfiguration {

    @Value("${sentry.dsn}")
    private String dsn;

    @PostConstruct
    public void init() {
        Sentry.init(options -> {
            options.setDsn(dsn);
            options.setDebug(true);                 // 디버깅 모드 활성화
            options.setTracesSampleRate(1.0);       // 성능 모니터링 샘플링 비율 설정 (0.0 - 1.0)
            options.setAttachStacktrace(true);      // 스택 트레이스 포함
        });

        // Sentry 초기화가 완료된 후 테스트 예외 발생
        // testSentry();
    }

    private void testSentry() {
        try {
            throw new RuntimeException("Test exception for Sentry configuration");
        } catch (Exception e) {
            Sentry.captureException(e);
        }
    }
}
