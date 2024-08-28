package com.nemo.oceanAcademy.domain.schedule.dataAccess.entity;

import com.nemo.oceanAcademy.domain.classroom.dataAccess.entity.Classroom;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "schedules")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Schedule {

    // PK 일정 아이디 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK 강의실 아이디
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(nullable = false, name = "classroom_id")
    @NotNull(message = "Classroom must not be null")
    private Classroom classroom;

    // 강의 일정 내용
    @Column(nullable = false, length = 700)
    @Size(min = 1, max = 500, message = "Content must be between 1 and 500 characters")
    @NotNull(message = "Content must not be null")
    private String content;

    // 강의 일정 날짜, 수동 설정
    @Column(nullable = false)
    @NotNull(message = "Date must not be null")
    private LocalDate date;

    // 강의 시작 시각, 수동 설정
    @Column(nullable = false, name = "start_time")
    @NotNull(message = "Start time must not be null")
    private LocalTime startTime;

    // 강의 종료 시각, 수동 설정
    @Column(nullable = false, name = "end_time")
    @NotNull(message = "end time must not be null")
    private LocalTime endTime;
}
