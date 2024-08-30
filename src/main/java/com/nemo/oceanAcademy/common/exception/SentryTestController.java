package com.nemo.oceanAcademy.common.exception;
import io.sentry.Sentry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class SentryTestController {

    @GetMapping("/sentry")
    public String triggerSentryError() {
        try {
            // 일부러 예외 발생
            throw new RuntimeException("Test exception for Sentry triggered via API");
        } catch (Exception e) {
            Sentry.captureException(e);  // Sentry에 예외 전송
            return "Exception sent to Sentry";
        }
    }
}
