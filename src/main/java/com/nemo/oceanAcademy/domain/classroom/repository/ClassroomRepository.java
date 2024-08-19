package com.nemo.oceanAcademy.domain.classroom.repository;

import com.nemo.oceanAcademy.domain.classroom.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
}
