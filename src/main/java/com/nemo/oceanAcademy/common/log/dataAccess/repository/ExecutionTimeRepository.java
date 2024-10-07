package com.nemo.oceanAcademy.common.log.dataAccess.repository;

import com.nemo.oceanAcademy.common.log.dataAccess.entity.ExecutionTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecutionTimeRepository extends JpaRepository<ExecutionTime, Long> {
}