package com.nemo.oceanAcademy.domain.chat.repository;

import com.nemo.oceanAcademy.domain.chat.entity.Chat;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChatRepository extends ReactiveMongoRepository<Chat, String> {
    Flux<Chat>findAllByRoomId(Long roomId);
}
