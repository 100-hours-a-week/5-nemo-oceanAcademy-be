package com.nemo.oceanAcademy.domain.classroom.dataAccess.repository;

import com.nemo.oceanAcademy.domain.classroom.dataAccess.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
}
