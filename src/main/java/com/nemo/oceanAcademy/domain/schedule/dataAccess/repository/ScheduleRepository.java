package com.nemo.oceanAcademy.domain.schedule.dataAccess.repository;

import com.nemo.oceanAcademy.domain.schedule.dataAccess.entity.Schedule;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 특정 강의실에 속한 모든 스케줄 조회
    List<Schedule> findByClassroom(Classroom classroom);

    // 특정 강의실에 속한 특정 스케줄 조회
    Optional<Schedule> findByIdAndClassroom(Long id, Classroom classroom);
}
