package com.nemo.oceanAcademy.common.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RedisWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(RedisWebSocketHandler.class);

    private final RedisTemplate<String, String> redisTemplate;
    private static final String SESSION_KEY_PREFIX = "websocket:session:";

    // 서버에서 관리할 세션 목록
    private final Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    public RedisWebSocketHandler(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 서버 시작 시 Redis에서 세션 복구
    @PostConstruct
    public void restoreSessionsFromRedis() {
        Set<String> sessionKeys = redisTemplate.keys(SESSION_KEY_PREFIX + "*");
        if (sessionKeys != null) {
            for (String key : sessionKeys) {
                String sessionId = redisTemplate.opsForValue().get(key);
                if (sessionId != null) {
                    restoreSession(sessionId);  // 세션 복구
                }
            }
            logger.info("Restored {} sessions from Redis", sessionKeys.size());
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 세션 정보를 Redis에 저장
        redisTemplate.opsForValue().set(SESSION_KEY_PREFIX + session.getId(), session.getId());
        activeSessions.put(session.getId(), session);
        logger.info("WebSocket connection established and session stored in Redis: {}", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 세션이 종료되면 Redis에서 삭제
        redisTemplate.delete(SESSION_KEY_PREFIX + session.getId());
        activeSessions.remove(session.getId());
        logger.info("WebSocket connection closed and session removed from Redis: {}", session.getId());
    }

    // Redis에서 세션을 복구하는 메서드
    public void restoreSession(String sessionId) {
        WebSocketSession session = activeSessions.get(sessionId);
        if (session != null && session.isOpen()) {
            logger.info("Session {} is already active and open", sessionId);
        } else {
            logger.warn("No active session found for session ID: {}", sessionId);
            // 필요한 경우 새 WebSocket 연결을 여는 로직 추가 가능
        }
    }
}
