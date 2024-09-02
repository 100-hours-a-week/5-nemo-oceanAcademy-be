package com.nemo.oceanAcademy.domain.chat.application.interceptor;

import com.nemo.oceanAcademy.domain.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FilterChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtUtils;
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        System.out.println("message:" + message);
        System.out.println("헤더 : " + message.getHeaders());
        System.out.println("토큰" + accessor.getNativeHeader("Authorization"));
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authorization = jwtUtils.extractJwt(accessor);
            if (authorization != null && authorization.startsWith("Bearer ")) { // 인증 시도
                authorization = authorization.substring(7);

                if(jwtUtils.validateToken(authorization)){
                    System.out.println("authorization = " + authorization);
                    return message;
                }
                else throw new MessagingException("Invalid token "); // 예외를 던져 연결을 중단

            } else {
                throw new MessagingException("Missing or invalid Authorization header"); // 토큰이 없거나 잘못된 형식일 경우 예외 발생
            }
        }
        return message;
    }
}
