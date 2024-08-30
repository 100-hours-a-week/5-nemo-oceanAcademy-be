package com.nemo.oceanAcademy.config;
import io.sentry.Sentry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
class SentryConfiguration {

    @Value("${sentry.dsn}")
    private String dsn;

    @PostConstruct
    public void init() {
        Sentry.init(options -> {
            options.setDsn(dsn);
            // 추가 설정

        });
    }
}
