package com.nemo.oceanAcademy.config;
import com.nemo.oceanAcademy.domain.chat.application.exception.StompExceptionHandler;
import com.nemo.oceanAcademy.domain.chat.application.interceptor.FilterChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@ComponentScan(basePackages = {"com.nemo.oceanAcademy.config", "com.nemo.oceanAcademy.domain.chat"})
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompExceptionHandler stompExceptionHandler;
    private final FilterChannelInterceptor filterChannelInterceptor;
    public void configureMessageBroker(MessageBrokerRegistry config){ //메시지 브로커를 설정하는 부분
        //spring이 제공해주는 기본 brocker, 추후에 RabbitMQ나 kafka를 사용할 수 있는
        config.enableSimpleBroker("/topic"); //발행자가 "/topic"의 경로로 메시지를 주면 구독자들에게 전달

        //"/app" 접두사가 붙은 경로는 @MessageMapping이 붙은곳을 타겟을 한다는 설정
        config.setApplicationDestinationPrefixes("/app"); // 발행자가 "/app"의 경로로 메시지를 주면 가공을 해서 구독자들에게 전달, GreetingController로 감
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //인자로 들어가는 url은 첫 핸드쉐이크 주소
        registry.setErrorHandler(stompExceptionHandler)
                .addEndpoint("/ws")
                .addInterceptors()
                .setAllowedOriginPatterns("*")
                .withSockJS(); // 커넥션을 맺는 경로 설정. 만약 WebSocket을 사용할 수 없는 브라우저라면 다른 방식을 사용하도록 설정
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(securityContextChannelInterceptor(),filterChannelInterceptor);
    }

    // SecurityContextChannelInterceptor Bean 설정
    @Bean
    public SecurityContextChannelInterceptor securityContextChannelInterceptor() {
        return new SecurityContextChannelInterceptor();
    }
}
