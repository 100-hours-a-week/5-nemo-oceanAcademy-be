package com.nemo.oceanAcademy.domain.classroom.application.service;
import com.nemo.oceanAcademy.common.s3.S3ImageUtils;
import com.nemo.oceanAcademy.domain.classroom.application.dto.*;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.entity.Classroom;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.entity.PopularityRank;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.repository.ClassroomRepository;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.repository.PopularityRankRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private PopularityRankRepository popularityRankRepository;

    private final ClassroomRepository classroomRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final S3ImageUtils imageUtils;


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
    public List<ClassroomResponseDto> getAllClassrooms(Pageable pageable) {
        try {
            return classroomRepository.findAllWithJoins(pageable).stream()
                    .map(this::toClassroomResponseDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Sentry.captureException(e);
            throw e;
        }
    }

    // top10강의 조회
    public List<ClassroomResponseDto> findTop10Classrooms() {
        Pageable pageable = PageRequest.of(0, 10);

        // 1. 인기 순위에 따라 Top 10 강의 조회
        //List<ClassroomResponseDto> topRankedClassrooms = popularityRankRepository.findTopRankedClassrooms();
        List<PopularityRank> a = popularityRankRepository.findAllByOrderByRankingAsc(pageable);
        // 2. DTO 리스트를 생성합니다.
        List<ClassroomResponseDto> classroomResponseDtos = new ArrayList<>();

        for (PopularityRank rank : a) {
            System.out.println(rank.getClassroom().getId());
        }

        // 3. 각 PopularityRank에서 Classroom을 가져와 DTO를 생성하고 리스트에 추가
        for (PopularityRank rank : a) {
            System.out.println(rank.getClassroom().getId());
            ClassroomResponseDto dto = new ClassroomResponseDto(rank.getClassroom()); // Classroom을 이용해 DTO 생성
            System.out.println(dto.getId());
            classroomResponseDtos.add(dto); // DTO를 리스트에 추가
        }
        return classroomResponseDtos;


        // 2. 조회된 결과가 부족하거나 비어 있으면 랜덤 클래스를 가져옴
//        if (topRankedClassrooms.size() < 10) {
//            int remainingSlots = 10 - topRankedClassrooms.size();
//            // 랜덤 클래스를 추가로 가져오는 로직
//            List<ClassroomResponseDto> randomClassrooms = classroomRepository.findRandomClassrooms(PageRequest.of(0, remainingSlots));
//
//            // 3. 기존 인기 순위 클래스와 랜덤 클래스를 합침
//            topRankedClassrooms.addAll(randomClassrooms);
//        }

    }

    // 강의 필터링 및 페이징 처리
    public List<ClassroomResponseDto> getFilteredClassrooms(String target, Integer categoryId, String userId, int page, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(page, pageSize);
            if (target != null) {
                switch (target) {
                    case "live":
                        return classroomRepository.findLiveClassrooms(categoryId, pageable);
                    case "enrolled":
                        return classroomRepository.findEnrolledClassrooms(categoryId, userId, pageable);
                    case "created":
                        return classroomRepository.findCreatedClassrooms(categoryId, userId, pageable);
                    case "topten":
                        return findTop10Classrooms();//getTopClassrooms(0,10); //getTop10ClassroomsByRanking(); //findTop10Classrooms();
                    default:
                        return getAllClassrooms(pageable);
                }
            }

            return categoryId != null
                    ? classroomRepository.findClassroomsByCategoryId(categoryId, pageable)
                    : getAllClassrooms(pageable);
        } catch (Exception e) {
            Sentry.captureException(e);
            throw e;
        }
    }

    // 새로운 강의 생성
    public ClassroomResponseDto createClassroom(ClassroomCreateDto classroomCreateDto, MultipartFile imagefile) {
        try {
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
            if (imagefile != null && !imagefile.isEmpty()) {
                String fileName = imageUtils.saveFileToS3(imagefile);
                classroom.setBannerImagePath(fileName);
            }

            classroomRepository.save(classroom);
            return toClassroomResponseDto(classroom);
        } catch (Exception e) {
            Sentry.captureException(e);
            throw e;
        }
    }

    // 강의실 정보 업데이트
    public ClassroomResponseDto updateClassroom(Long classId, ClassroomUpdateDto classroomUpdateDto, MultipartFile imagefile) {
        try {
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
            if (classroomUpdateDto.getCategoryId() != null) {
                Integer categoryId = classroomUpdateDto.getCategoryId();
                // Category 객체를 Repository를 통해 조회
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("Category not found"));
                classroom.setCategory(category); // 조회된 Category 객체를 classroom에 설정
            }
            if (classroomUpdateDto.getObject() != null) classroom.setObject(classroomUpdateDto.getObject());
            if (classroomUpdateDto.getDescription() != null) classroom.setDescription(classroomUpdateDto.getDescription());
            if (classroomUpdateDto.getIsActive() != null) classroom.setIsActive(classroomUpdateDto.getIsActive());
            if (classroomUpdateDto.getInstructorInfo() != null) classroom.setInstructorInfo(classroomUpdateDto.getInstructorInfo());
            if (classroomUpdateDto.getPrerequisite() != null) classroom.setPrerequisite(classroomUpdateDto.getPrerequisite());
            if (classroomUpdateDto.getAnnouncement() != null) classroom.setAnnouncement(classroomUpdateDto.getAnnouncement());

            // 배너 이미지 파일 업데이트
            if (imagefile != null && !imagefile.isEmpty()) {

                /* // 기존 배너 이미지 삭제 (더미 데이터로 인해 에러가 발생할 것 같아 주석 처리 해둡니다.
                if (classroom.getBannerImagePath() != null){
                    imageUtils.deleteFileFromS3(classroom.getBannerImagePath());
                }
                */

                String fileName = imageUtils.saveFileToS3(imagefile);
                classroom.setBannerImagePath(fileName);
            }

            classroomRepository.save(classroom);
            return toClassroomResponseDto(classroom);
        } catch (Exception e) {
            Sentry.captureException(e);
            throw e;
        }
    }

    // 개별 강의실 조회
    public ClassroomResponseDto getClassroomById(Long classId) {
        try {
            Classroom classroom = classroomRepository.findByIdWithJoins(classId)
                    .orElseThrow(() -> new ResourceNotFoundException("해당하는 ID(" + classId + ")의 강의를 찾을 수 없습니다.", "Classroom not found"));
            return toClassroomResponseDto(classroom);
        } catch (Exception e) {
            Sentry.captureException(e);
            throw e;
        }
    }

    // Soft Delete 방식으로 강의실 삭제
    public void deleteClassroom(Long classId) {
        try {
            Classroom classroom = classroomRepository.findById(classId)
                    .orElseThrow(() -> new ResourceNotFoundException("해당하는 ID(" + classId + ")의 강의를 찾을 수 없습니다.", "Classroom not found"));
            classroom.setDeletedAt(LocalDateTime.now());
            classroomRepository.save(classroom);
        } catch (Exception e) {
            Sentry.captureException(e);
            throw e;
        }
    }

    // 사용자의 강의실 역할 확인
    public String getUserRoleInClassroom(Long classId, String userId) {
        try {
            Classroom classroom = classroomRepository.findById(classId)
                    .orElseThrow(() -> new ResourceNotFoundException("해당하는 ID(" + classId + ")의 강의를 찾을 수 없습니다.", "Classroom not found"));

            if (classroom.getUser().getId().equals(userId)) return "강사";

            return participantRepository.existsByClassroomIdAndUserId(classId, userId) ? "수강생" : "관계없음";
        } catch (Exception e) {
            Sentry.captureException(e);
            throw e;
        }
    }

    // 강의를 듣는 수강생 리스트 조회
    public List<ParticipantResponseDto> getClassroomStudents(Long classId) {
        try {
            Classroom classroom = classroomRepository.findById(classId)
                    .orElseThrow(() -> new ResourceNotFoundException("해당하는 ID(" + classId + ")의 강의를 찾을 수 없습니다.", "Classroom not found"));

            List<Participant> participants = participantRepository.findParticipantsByClassroomId(classId);

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
        } catch (Exception e) {
            Sentry.captureException(e);
            throw e;
        }
    }

    // 강의실 라이브 정보 불러오기
    public ClassroomLiveStatusDto getClassroomIsLive(Long classId) {
        try {
            Classroom classroom = classroomRepository.findById(classId)
                    .orElseThrow(() -> {
                        ResourceNotFoundException exception = new ResourceNotFoundException(
                                "해당하는 ID(" + classId + ")의 강의를 찾을 수 없습니다.",
                                "Classroom not found"
                        );

                        Sentry.withScope(scope -> {
                            scope.setTag("classroom_id", classId.toString());
                            scope.setExtra("error_type", "Classroom Not Found");
                            Sentry.captureException(exception);
                        });

                        return exception;
                    });

            return ClassroomLiveStatusDto.builder()
                    .isActive(classroom.getIsActive())
                    .build();

        } catch (Exception e) {
            Sentry.captureException(e);
            throw e;
        }
    }

    // 강의실 라이브 정보 수정하기
    public ClassroomLiveStatusDto changeClassroomIsLive(Long classId) {
        try {
            Classroom classroom = classroomRepository.findById(classId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "해당하는 ID(" + classId + ")의 강의를 찾을 수 없습니다.",
                            "Classroom not found"
                    ));

            classroom.setIsActive(!classroom.getIsActive());
            classroomRepository.save(classroom);

            return ClassroomLiveStatusDto.builder()
                    .isActive(classroom.getIsActive())
                    .build();

        } catch (Exception e) {
            Sentry.captureException(e);
            throw e;
        }
    }

    // 강의실 대시보드 정보 및 스케줄 가져오기
    public ClassroomDashboardDto getClassroomDashboard(Long classId, String userId) {
        try {
            Classroom classroom = classroomRepository.findById(classId)
                    .orElseThrow(() -> {
                        ResourceNotFoundException exception = new ResourceNotFoundException(
                                "해당하는 ID(" + classId + ")의 강의를 찾을 수 없습니다.",
                                "Classroom not found"
                        );

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
            Sentry.captureException(e);
            throw e;
        }
    }

    // 수강 신청
    public void enrollParticipant(String userId, Long classId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("해당하는 ID(" + userId + ")의 사용자를 찾을 수 없습니다.", "User not found"));
            Classroom classroom = classroomRepository.findById(classId)
                    .orElseThrow(() -> new ResourceNotFoundException("해당하는 ID(" + classId + ")의 강의를 찾을 수 없습니다.", "Classroom not found"));

            Participant participant = Participant.builder()
                    .user(user)
                    .classroom(classroom)
                    .createdAt(LocalDateTime.now())
                    .build();

            participantRepository.save(participant);
        } catch (Exception e) {
            Sentry.captureException(e);
            throw e;
        }
    }
}
