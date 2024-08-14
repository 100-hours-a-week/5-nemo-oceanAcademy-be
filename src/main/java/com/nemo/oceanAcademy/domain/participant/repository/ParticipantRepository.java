package com.nemo.oceanAcademy.domain.participant.repository;

import com.nemo.oceanAcademy.domain.participant.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
