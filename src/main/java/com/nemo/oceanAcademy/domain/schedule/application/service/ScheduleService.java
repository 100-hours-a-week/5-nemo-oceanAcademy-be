package com.nemo.oceanAcademy.domain.schedule.application.service;
import com.nemo.oceanAcademy.domain.schedule.application.dto.ScheduleDto;
import com.nemo.oceanAcademy.domain.schedule.dataAccess.entity.Schedule;
import com.nemo.oceanAcademy.domain.schedule.dataAccess.repository.ScheduleRepository;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.entity.Classroom;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.repository.ClassroomRepository;
import com.nemo.oceanAcademy.common.exception.ResourceNotFoundException;
import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ScheduleService는 강의 일정 관련 비즈니스 로직을 처리합니다.
 * - 강의 일정 조회, 생성, 삭제 로직을 담당합니다.
 */
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ClassroomRepository classroomRepository;

    /**
     * 강의 일정 목록 조회
     *
     * @param classId 강의 ID
     * @param userId 사용자 ID (인증된 사용자)
     * @return List<ScheduleDto> 강의 일정 목록
     */
    public List<ScheduleDto> getSchedulesByClassId(Long classId, String userId) {
        try {
            Classroom classroom = classroomRepository.findById(classId)
                    .orElseThrow(() -> new ResourceNotFoundException("해당하는 ID(" + classId + ")의 강의실을 찾을 수 없습니다.", "Classroom not found"));

            return scheduleRepository.findByClassroom(classroom).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            Sentry.captureException(e);
            throw e;
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("강의 일정 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 강의 일정 생성
     *
     * @param classId 강의 ID
     * @param scheduleDto 생성할 일정 정보
     * @param userId 사용자 ID (인증된 사용자)
     * @return ScheduleDto 생성된 강의 일정
     */
    public ScheduleDto createSchedule(Long classId, ScheduleDto scheduleDto, String userId) {
        try {
            Classroom classroom = classroomRepository.findById(classId)
                    .orElseThrow(() -> new ResourceNotFoundException("해당하는 ID(" + classId + ")의 강의실을 찾을 수 없습니다.", "Classroom not found"));

            Schedule schedule = convertToEntity(scheduleDto);
            schedule.setClassroom(classroom);
            Schedule savedSchedule = scheduleRepository.save(schedule);

            return convertToDto(savedSchedule);
        } catch (ResourceNotFoundException e) {
            Sentry.captureException(e);
            throw e;
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("강의 일정 생성 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 강의 일정 삭제
     *
     * @param classId 강의 ID
     * @param scheduleId 삭제할 일정 ID
     * @param userId 사용자 ID (인증된 사용자)
     */
    public void deleteSchedule(Long classId, Long scheduleId, String userId) {
        try {
            Classroom classroom = classroomRepository.findById(classId)
                    .orElseThrow(() -> new ResourceNotFoundException("해당하는 ID(" + classId + ")의 강의실을 찾을 수 없습니다.", "Classroom not found"));
            Schedule schedule = scheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> new ResourceNotFoundException("해당하는 ID(" + scheduleId + ")의 일정을 찾을 수 없습니다.", "Schedule not found"));
            scheduleRepository.delete(schedule);
        } catch (ResourceNotFoundException e) {
            Sentry.captureException(e);
            throw e;
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("강의 일정 삭제 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 엔티티를 DTO로 변환
     *
     * @param schedule 변환할 일정 엔티티
     * @return ScheduleDto 변환된 DTO
     */
    private ScheduleDto convertToDto(Schedule schedule) {
        return ScheduleDto.builder()
                .id(schedule.getId())
                .classId(schedule.getClassroom().getId())
                .content(schedule.getContent())
                .date(schedule.getDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .build();
    }

    /**
     * DTO를 엔티티로 변환
     *
     * @param scheduleDto 변환할 일정 DTO
     * @return Schedule 변환된 엔티티
     */
    private Schedule convertToEntity(ScheduleDto scheduleDto) {
        return Schedule.builder()
                .content(scheduleDto.getContent())
                .date(scheduleDto.getDate())
                .startTime(scheduleDto.getStartTime())
                .endTime(scheduleDto.getEndTime())
                .build();
    }
}
