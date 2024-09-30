package com.nemo.oceanAcademy.domain.participant.dataAccess.repository;
import com.nemo.oceanAcademy.domain.participant.dataAccess.entity.Participant;
import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    boolean existsByClassroomIdAndUserId(Long classroomId, String userId);

    // classroom_id로 수강생 목록 조회
    @Query("SELECT p FROM Participant p WHERE p.classroom.id = :classroomId")
    List<Participant> findParticipantsByClassroomId(@Param("classroomId") Long classroomId);

    // 강의의 수강생 수 반환
    @Query("SELECT COUNT(p) FROM Participant p WHERE p.classroom.id = :classroomId")
    long countParticipantsInClassroom(@Param("classroomId") Long classroomId);
}
