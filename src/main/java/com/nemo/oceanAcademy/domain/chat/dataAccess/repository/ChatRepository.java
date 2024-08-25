package com.nemo.oceanAcademy.domain.chat.dataAccess.repository;

import com.nemo.oceanAcademy.domain.chat.dataAccess.entity.Chat;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ChatRepository extends ReactiveMongoRepository<Chat, String> {
    Flux<Chat> findAllByRoomId(Long roomId);

}
