package com.nemo.oceanAcademy.domain.schedule.application.service;
import com.nemo.oceanAcademy.domain.schedule.application.dto.ScheduleDto;
import com.nemo.oceanAcademy.domain.schedule.dataAccess.entity.Schedule;
import com.nemo.oceanAcademy.domain.schedule.dataAccess.repository.ScheduleRepository;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.entity.Classroom;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.repository.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ClassroomRepository classroomRepository;

    // 강의 일정 불러오기
    public List<ScheduleDto> getSchedulesByClassId(Long classId, String userId) {
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의실을 찾을 수 없습니다."));
        return scheduleRepository.findByClassroom(classroom).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 강의 일정 생성하기
    public ScheduleDto createSchedule(Long classId, ScheduleDto scheduleDto, String userId) {
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의실을 찾을 수 없습니다."));
        Schedule schedule = convertToEntity(scheduleDto);
        schedule.setClassroom(classroom);
        Schedule savedSchedule = scheduleRepository.save(schedule);
        return convertToDto(savedSchedule);
    }

    // 강의 일정 삭제하기
    public void deleteSchedule(Long classId, Long scheduleId, String userId) {
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의실을 찾을 수 없습니다."));
        Schedule schedule = scheduleRepository.findByIdAndClassroom(scheduleId, classroom)
                .orElseThrow(() -> new IllegalArgumentException("해당 스케줄을 찾을 수 없습니다."));
        scheduleRepository.delete(schedule);
    }

    // 엔티티를 DTO로 변환
    private ScheduleDto convertToDto(Schedule schedule) {
        return ScheduleDto.builder()
                .id(schedule.getId())
                .classId(schedule.getClassroom().getId())
                .content(schedule.getContent())
                .date(schedule.getDate())
                .startTime(schedule.getStartTime())
                .finishTime(schedule.getFinishTime())
                .build();
    }

    // DTO를 엔티티로 변환
    private Schedule convertToEntity(ScheduleDto scheduleDto) {
        return Schedule.builder()
                .content(scheduleDto.getContent())
                .date(scheduleDto.getDate())
                .startTime(scheduleDto.getStartTime())
                .finishTime(scheduleDto.getFinishTime())
                .build();
    }
}
