package com.nemo.oceanAcademy.domain.classroom.application.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class TargetAccessChecker {

    public boolean requiresAuthentication(HttpServletRequest request) {
        String target = request.getParameter("target");

        // target이 "enrolled" 또는 "created"일 경우에만 인증을 요구
        return "enrolled".equals(target) || "created".equals(target);
    }
}
