package com.nemo.oceanAcademy.domain.participant.dataAccess.repository;

import com.nemo.oceanAcademy.domain.participant.dataAccess.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
