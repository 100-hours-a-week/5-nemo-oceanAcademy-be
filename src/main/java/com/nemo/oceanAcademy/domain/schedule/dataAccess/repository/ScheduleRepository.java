package com.nemo.oceanAcademy.domain.schedule.dataAccess.repository;

import com.nemo.oceanAcademy.domain.schedule.dataAccess.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
