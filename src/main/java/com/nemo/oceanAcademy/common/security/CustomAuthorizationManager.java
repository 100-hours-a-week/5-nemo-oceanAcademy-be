package com.nemo.oceanAcademy.common.security;

import com.nemo.oceanAcademy.domain.classroom.application.controller.TargetAccessChecker;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.function.Supplier;

@Component
public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final TargetAccessChecker targetAccessChecker;

    public CustomAuthorizationManager(TargetAccessChecker targetAccessChecker) {
        this.targetAccessChecker = targetAccessChecker;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext requestAuthorizationContext) {
        HttpServletRequest request = requestAuthorizationContext.getRequest();
        String target = request.getParameter("target");

        boolean requiresAuth = targetAccessChecker.requiresAuthentication(request);

        if (!requiresAuth) {
            return new AuthorizationDecision(true);  // 인증 필요 없음
        }

        Authentication auth = authentication.get();
        boolean isAuthenticated = auth != null && auth.isAuthenticated();
        return new AuthorizationDecision(isAuthenticated);
    }
}
