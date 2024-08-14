package com.nemo.oceanAcademy.domain.schedule.repository;

import com.nemo.oceanAcademy.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
