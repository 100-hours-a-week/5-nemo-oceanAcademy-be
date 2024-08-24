package com.nemo.oceanAcademy.domain.chat.service;

import com.nemo.oceanAcademy.domain.chat.entity.Chat;
import com.nemo.oceanAcademy.domain.chat.repository.ChatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class ChatService {
    private final ChatRepository chatRepository;
    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }
    @Transactional
    public Flux<Chat> findChatMessages(Long id) {
        return chatRepository.findAllByRoomId(id);
    }

    @Transactional
    // 메시지를 저장하는 메서드
    public Mono<Chat> saveChatMessage(Chat chat) {
        chat.setCreatedDate(new Date()); // 메시지 생성 날짜 설정
        return chatRepository.save(chat);
    }
}
