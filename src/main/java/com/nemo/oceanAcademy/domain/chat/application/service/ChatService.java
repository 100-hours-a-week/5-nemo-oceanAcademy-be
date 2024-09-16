package com.nemo.oceanAcademy.domain.chat.application.service;
import com.nemo.oceanAcademy.domain.chat.dataAccess.entity.Chat;
import com.nemo.oceanAcademy.domain.chat.dataAccess.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final SequenceGeneratorService sequenceGeneratorService; // 시퀀스 생성 서비스 추가

    @Transactional
    public Flux<Chat> findChatMessages(Long id) {
        return chatRepository.findAllByRoomId(id);
    }

    @Transactional
    // 메시지를 저장하는 메서드
    public Mono<Chat> saveChatMessage(Chat chat) {
        // 채팅 메시지가 빈 값이거나 공백만 있는 경우 메시지를 무시
        if (chat.getContent() == null || chat.getContent().trim().isEmpty()) {
            return Mono.empty(); // 빈 메시지일 경우 아무 작업도 하지 않음
        }

        String chatId = sequenceGeneratorService.generateChatId(chat.getRoomId());  // 자동 증가 ID를 생성하여 설정
        chat.setId(chatId);                         // 생성된 ID를 Chat 엔티티에 설정
        chat.setCreatedDate(new Date());            // 메시지 생성 날짜 설정
        return chatRepository.save(chat);
    }
}
