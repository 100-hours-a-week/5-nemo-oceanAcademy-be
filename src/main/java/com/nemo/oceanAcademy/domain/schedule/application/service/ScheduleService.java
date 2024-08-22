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

    // 특정 강의실에 속한 모든 스케줄 조회 (인증된 사용자 기반)
    public List<ScheduleDto> getSchedulesByClassId(Long classId, String userId) {
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의실을 찾을 수 없습니다."));

        // 인증된 사용자와 연관된 스케줄만 반환하는 로직 추가
        // 예: 사용자가 강의실의 소유자인지, 강사인지, 학생인지 확인하는 로직 추가 가능

        return scheduleRepository.findByClassroom(classroom).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 특정 강의실에 속한 스케줄 조회 (인증된 사용자 기반)
    public ScheduleDto getScheduleByIdAndClassId(Long classId, Long scheduleId, String userId) {
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의실을 찾을 수 없습니다."));

        // 인증된 사용자와 연관된 스케줄인지 확인하는 로직 추가 가능

        Schedule schedule = scheduleRepository.findByIdAndClassroom(scheduleId, classroom)
                .orElseThrow(() -> new IllegalArgumentException("해당 스케줄을 찾을 수 없습니다."));
        return convertToDto(schedule);
    }

    // 특정 강의실에 스케줄 생성 (인증된 사용자 기반)
    public ScheduleDto createSchedule(Long classId, ScheduleDto scheduleDto, String userId) {
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의실을 찾을 수 없습니다."));

        // 인증된 사용자가 강의실에 대한 권한을 가지고 있는지 확인하는 로직 추가 가능

        Schedule schedule = convertToEntity(scheduleDto);
        schedule.setClassroom(classroom);
        Schedule savedSchedule = scheduleRepository.save(schedule);
        return convertToDto(savedSchedule);
    }

    // 특정 강의실에 속한 스케줄 삭제 (인증된 사용자 기반)
    public void deleteSchedule(Long classId, Long scheduleId, String userId) {
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의실을 찾을 수 없습니다."));

        // 인증된 사용자가 해당 스케줄을 삭제할 수 있는 권한을 가지고 있는지 확인하는 로직 추가 가능

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
