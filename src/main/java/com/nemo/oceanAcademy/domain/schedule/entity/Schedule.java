package com.nemo.oceanAcademy.domain.schedule.entity;

import com.nemo.oceanAcademy.domain.clazz.entity.Clazz;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "schedules")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Schedule {

    // PK 일정 아이디 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK 강의실 아이디
    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Clazz clazz;

    // 강의 일정 날짜
    private LocalDate date;

    // 강의 시작 시각
    @Column(name = "start_time", nullable = true)
    private LocalTime startTime;

    // 강의 종료 시각
    @Column(name = "finish_time", nullable = true)
    private LocalTime finishTime;

    // 강의 일정 내용
    @Column(columnDefinition = "TEXT")
    private String content;

    @PrePersist
    protected void prePersist() {
        if (this.startTime == null) {
            this.startTime = LocalTime.now();
        }
    }

    // 강의 종료 시각을 수동으로 설정하는 메서드
    public void markAsFinished() {
        this.finishTime = LocalTime.now();
    }
}
