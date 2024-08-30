package com.nemo.oceanAcademy.domain.classroom.application.service;
import com.nemo.oceanAcademy.domain.classroom.application.dto.*;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.entity.Classroom;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.repository.ClassroomRepository;
import com.nemo.oceanAcademy.domain.participant.application.dto.ParticipantResponseDto;
import com.nemo.oceanAcademy.domain.category.dataAccess.entity.Category;
import com.nemo.oceanAcademy.domain.category.dataAccess.repository.CategoryRepository;
import com.nemo.oceanAcademy.domain.participant.dataAccess.entity.Participant;
import com.nemo.oceanAcademy.domain.participant.dataAccess.repository.ParticipantRepository;
import com.nemo.oceanAcademy.domain.schedule.application.dto.ScheduleDto;
import com.nemo.oceanAcademy.domain.schedule.dataAccess.repository.ScheduleRepository;
import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;
import com.nemo.oceanAcademy.domain.user.dataAccess.repository.UserRepository;
import com.nemo.oceanAcademy.common.exception.ResourceNotFoundException;
import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 공통 변환 메서드
    private ClassroomResponseDto toClassroomResponseDto(Classroom classroom) {
        return ClassroomResponseDto.builder()
                .id(classroom.getId())
                .categoryId(classroom.getCategory().getId())
                .userId(classroom.getUser().getId())
                .instructor(classroom.getUser().getNickname())
                .category(classroom.getCategory().getName())
                .name(classroom.getName())
                .object(classroom.getObject())
                .description(classroom.getDescription())
                .instructorInfo(classroom.getInstructorInfo())
                .prerequisite(classroom.getPrerequisite())
                .announcement(classroom.getAnnouncement())
                .bannerImagePath(classroom.getBannerImagePath())
                .isActive(classroom.getIsActive())
                .build();
    }

    // 전체 강의실 조회
    public List<ClassroomResponseDto> getAllClassrooms() {
        return classroomRepository.findAllWithJoins().stream()
                .map(this::toClassroomResponseDto)
                .collect(Collectors.toList());
    }

    // 강의 필터링 및 페이징 처리
    public List<ClassroomResponseDto> getFilteredClassrooms(String target, Integer categoryId, String userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);

        if (target != null) {
            switch (target) {
                case "live":
                    return classroomRepository.findLiveClassrooms(categoryId, pageable);
                case "enrolled":
                    return classroomRepository.findEnrolledClassrooms(categoryId, userId, pageable);
                case "created":
                    return classroomRepository.findCreatedClassrooms(categoryId, userId, pageable);
                default:
                    return getAllClassrooms();
            }
        }

        return categoryId != null
                ? classroomRepository.findClassroomsByCategoryId(categoryId, pageable)
                : getAllClassrooms();
    }

    // 새로운 강의 생성 TODO:배너이미지
    public ClassroomResponseDto createClassroom(ClassroomCreateDto classroomCreateDto, MultipartFile imagefile) {

        Category category = categoryRepository.findById(classroomCreateDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("해당하는 카테고리를 찾을 수 없습니다.", "Category not found"));
        User user = userRepository.findById(classroomCreateDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("해당하는 강사를 찾을 수 없습니다.", "Instructor not found"));

        Classroom classroom = Classroom.builder()
                .category(category)
                .user(user)
                .name(classroomCreateDto.getName())
                .object(classroomCreateDto.getObject())
                .description(classroomCreateDto.getDescription())
                .instructorInfo(classroomCreateDto.getInstructorInfo())
                .prerequisite(classroomCreateDto.getPrerequisite())
                .announcement(classroomCreateDto.getAnnouncement())
                .isActive(false)
                .build();

        // 배너 이미지 파일 업데이트
        System.out.println("imagefile service:" + imagefile);
        if (imagefile != null && !imagefile.isEmpty()) {
            String fileName = saveFileToDirectory(imagefile);
            System.out.println("imagefile fileName:" + imagefile);
            classroom.setBannerImagePath(fileName);
        }

        classroomRepository.save(classroom);
        return toClassroomResponseDto(classroom);
    }

    // 강의실 정보 업데이트 TODO:배너이미지
    public ClassroomResponseDto updateClassroom(Long classId, ClassroomUpdateDto classroomUpdateDto, MultipartFile imagefile) {
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> {
                    ResourceNotFoundException exception = new ResourceNotFoundException(
                            "해당하는 ID(" + classId + ")의 강의를 찾을 수 없습니다.",
                            "Classroom not found"
                    );
                    Sentry.captureException(exception);
                    return exception;
                });

        // 업데이트
        if (classroomUpdateDto.getName() != null) classroom.setName(classroomUpdateDto.getName());
        if (classroomUpdateDto.getObject() != null) classroom.setObject(classroomUpdateDto.getObject());
        if (classroomUpdateDto.getDescription() != null) classroom.setDescription(classroomUpdateDto.getDescription());
        if (classroomUpdateDto.getIsActive() != null) classroom.setIsActive(classroomUpdateDto.getIsActive());
        if (classroomUpdateDto.getInstructorInfo() != null) classroom.setInstructorInfo(classroomUpdateDto.getInstructorInfo());
        if (classroomUpdateDto.getPrerequisite() != null) classroom.setPrerequisite(classroomUpdateDto.getPrerequisite());
        if (classroomUpdateDto.getAnnouncement() != null) classroom.setAnnouncement(classroomUpdateDto.getAnnouncement());

        // 배너 이미지 파일 업데이트
        System.out.println("imagefile service:" + imagefile);
        if (imagefile != null && !imagefile.isEmpty()) {
            String fileName = saveFileToDirectory(imagefile);
            System.out.println("imagefile fileName:" + imagefile);
            classroom.setBannerImagePath(fileName);
        }

        classroomRepository.save(classroom);
        return toClassroomResponseDto(classroom);
    }

    // 개별 강의실 조회
    public ClassroomResponseDto getClassroomById(Long classId) {
        Classroom classroom = classroomRepository.findByIdWithJoins(classId)
                .orElseThrow(() -> new ResourceNotFoundException("해당하는 ID(" + classId + ")의 강의를 찾을 수 없습니다.", "Classroom not found"));
        return toClassroomResponseDto(classroom);
    }

    // Soft Delete 방식으로 강의실 삭제
    public void deleteClassroom(Long classId) {
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("해당하는 ID(" + classId + ")의 강의를 찾을 수 없습니다.", "Classroom not found"));
        classroom.setDeletedAt(LocalDateTime.now());
        classroomRepository.save(classroom);
    }

    /**
     * 파일을 로컬 디렉토리에 저장
     * @param imagefile 저장할 파일
     * @return String 저장된 파일 경로
     * @throws RuntimeException 파일 저장 중 오류 발생 시 예외 처리
     */
    private String saveFileToDirectory(MultipartFile imagefile) {
        try {
            // 고유한 파일 이름 생성 (UUID + 원래 파일명)
            String fileName = UUID.randomUUID().toString() + "-" + imagefile.getOriginalFilename();
            Path imagefilePath = Paths.get(uploadDir + "/" + fileName);
            Files.createDirectories(imagefilePath.getParent());                  // 저장 경로가 없으면 생성
            Files.write(imagefilePath, imagefile.getBytes());                    // 파일 저장
            System.out.println("imagefilePath:" + imagefilePath);
            return fileName;                                                     // 저장된 파일 이름 반환
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다.", e);                // 파일 저장 실패 시 예외 처리
        }
    }

    /*----------------------------------------------------------------------------------*/

    // 사용자의 강의실 역할 확인
    public String getUserRoleInClassroom(Long classId, String userId) {
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("해당하는 ID(" + classId + ")의 강의를 찾을 수 없습니다.", "Classroom not found"));

        if (classroom.getUser().getId().equals(userId)) return "강사";

        return participantRepository.existsByClassroomIdAndUserId(classId, userId) ? "수강생" : "관계없음";
    }

    // 강의를 듣는 수강생 리스트 조회
    public List<ParticipantResponseDto> getClassroomStudents(Long classId) {
        // 강의실을 찾지 못하면 예외 발생
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("해당하는 ID(" + classId + ")의 강의를 찾을 수 없습니다.", "Classroom not found"));

        // 수강생 목록 조회
        List<Participant> participants = participantRepository.findParticipantsByClassroomId(classId);

        // Participant 엔티티를 ParticipantResponseDto로 변환
        return participants.stream()
                .map(participant -> ParticipantResponseDto.builder()
                        .id(participant.getUser().getId())
                        .email(participant.getUser().getEmail())
                        .nickname(participant.getUser().getNickname())
                        .profileImagePath(participant.getUser().getProfileImagePath())
                        .createdAt(participant.getUser().getCreatedAt())
                        .deletedAt(participant.getUser().getDeletedAt())
                        .reviews(participant.getUser().getReviews())
                        .build())
                .collect(Collectors.toList());
    }

    public ClassroomDashboardDto getClassroomDashboard(Long classId, String userId) {
        try {
            Classroom classroom = classroomRepository.findById(classId)
                    .orElseThrow(() -> {
                        ResourceNotFoundException exception = new ResourceNotFoundException(
                                "해당하는 ID(" + classId + ")의 강의를 찾을 수 없습니다.",
                                "Classroom not found"
                        );

                        // Sentry에 예외 캡처와 함께 추가 정보 설정
                        Sentry.withScope(scope -> {
                            scope.setTag("classroom_id", classId.toString());
                            scope.setTag("user_id", userId);
                            scope.setExtra("error_type", "Classroom Not Found");
                            Sentry.captureException(exception);
                        });

                        return exception;
                    });

            String role = getUserRoleInClassroom(classId, userId);
            List<ScheduleDto> schedules = scheduleRepository.findSchedulesByClassroomId(classId);

            return ClassroomDashboardDto.builder()
                    .id(classroom.getId())
                    .userId(classroom.getUser().getId())
                    .categoryId(classroom.getCategory().getId())
                    .name(classroom.getName())
                    .object(classroom.getObject())
                    .description(classroom.getDescription())
                    .instructorInfo(classroom.getInstructorInfo())
                    .prerequisite(classroom.getPrerequisite())
                    .announcement(classroom.getAnnouncement())
                    .bannerImagePath(classroom.getBannerImagePath())
                    .isActive(classroom.getIsActive())
                    .role(role)
                    .schedules(schedules)
                    .build();

        } catch (Exception e) {
            // 예외 처리
            Sentry.captureException(e);
            throw e;
        }
    }
    /*----------------------------------------------------------------------------------*/


    // 수강 신청
    public void enrollParticipant(String userId, Long classId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("해당하는 ID(" + userId + ")의 사용자를 찾을 수 없습니다.", "User not found"));
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("해당하는 ID(" +  classId + ")의 강의를 찾을 수 없습니다.", "Classroom not found"));

        Participant participant = Participant.builder()
                .user(user)
                .classroom(classroom)
                .createdAt(LocalDateTime.now())
                .build();

        participantRepository.save(participant);
    }
}
