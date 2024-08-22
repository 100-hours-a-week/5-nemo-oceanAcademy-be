package com.nemo.oceanAcademy.domain.schedule.dataAccess.repository;
import com.nemo.oceanAcademy.domain.schedule.application.dto.ScheduleDto;
import com.nemo.oceanAcademy.domain.schedule.dataAccess.entity.Schedule;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 모든 강의 일정 조회
    List<Schedule> findByClassroom(Classroom classroom);

    // 특정 강의 일정 조회 - schedule id
    Optional<Schedule> findByIdAndClassroom(Long id, Classroom classroom);

    // 모든 강의 일정 DTO로 반환하는 쿼리
    @Query("SELECT new com.nemo.oceanAcademy.domain.schedule.application.dto.ScheduleDto(" +
            "s.id, s.classroom.id, s.content, s.date, s.startTime, s.finishTime) " +
            "FROM Schedule s WHERE s.classroom.id = :classroomId")
    List<ScheduleDto> findSchedulesByClassroomId(@Param("classroomId") Long classroomId);
}
