package com.nemo.oceanAcademy.domain.chat.application.controller;

import com.nemo.oceanAcademy.domain.chat.application.service.ChatService;
import com.nemo.oceanAcademy.domain.chat.dataAccess.entity.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate template;

    /**
     * 특정 채팅방의 모든 채팅 메시지를 조회
     * @param id  채팅방 ID
     * @return Mono<ResponseEntity<List<Chat>>>  채팅 메시지 목록을 Mono로 래핑하여 반환
     */
    @GetMapping("/find/chat/list/{id}")
    public Mono<ResponseEntity<List<Chat>>> find(@PathVariable("id")Long id){
        Flux<Chat> response = chatService.findChatMessages(id);
        return response.collectList().map(ResponseEntity::ok);
    }

    /**
     * 클라이언트로부터 채팅 메시지를 수신하고 이를 저장 후 구독자들에게 전송
     * @param chat  수신한 채팅 메시지 객체
     * @return Mono<Void>  처리 완료를 나타내는 Mono
     */
    @MessageMapping("/hello") // /app/hello 로 들어감
    @SendTo("/topic/messages")
    public Mono<Void> receiveMessage(Chat chat) {
        System.out.println(chat);
        return chatService.saveChatMessage(chat).flatMap(savedMessage -> {
            template.convertAndSend("/topic/greetings/" + chat.getRoomId(), savedMessage);
            return Mono.empty();
        });
    }
}
