package com.nemo.oceanAcademy.common.log.dataAccess.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExecutionTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startedAt;

    private String methodName;

    private Long executionTime; // 실행 시간(ms)

    private LocalDateTime executedAt; // 실행 시각

}

