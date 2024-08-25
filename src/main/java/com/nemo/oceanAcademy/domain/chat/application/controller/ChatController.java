package com.nemo.oceanAcademy.domain.chat.application.controller;

import com.nemo.oceanAcademy.domain.chat.application.service.ChatService;
import com.nemo.oceanAcademy.domain.chat.dataAccess.entity.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
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

    //이전 채팅 내용 조회
    @GetMapping("/find/chat/list/{id}")
    public Mono<ResponseEntity<List<Chat>>> find(@PathVariable("id")Long id){
        Flux<Chat> response = chatService.findChatMessages(id);
        return response.collectList().map(ResponseEntity::ok);
    }

    @MessageMapping("/hello") // /app/hello 로 들어감
    public Mono<Void> receiveMessage(Chat chat) {
        System.out.println(chat);
        return chatService.saveChatMessage(chat).flatMap(savedMessage -> {
            template.convertAndSend("/topic/greetings/" + chat.getRoomId(), savedMessage);
            return Mono.empty();
        });
    }

}
