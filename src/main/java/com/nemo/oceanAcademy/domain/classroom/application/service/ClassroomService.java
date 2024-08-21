package com.nemo.oceanAcademy.domain.classroom.application.service;

import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomCreateDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomUpdateDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomResponseDto;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.entity.Classroom;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.repository.ClassroomRepository;
import com.nemo.oceanAcademy.domain.category.dataAccess.entity.Category;
import com.nemo.oceanAcademy.domain.category.dataAccess.repository.CategoryRepository;
import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;
import com.nemo.oceanAcademy.domain.user.dataAccess.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // 새로운 강의 생성
    public ClassroomResponseDto createClassroom(ClassroomCreateDto classroomCreateDto) {
        // 카테고리와 사용자 유효성 검사
        Category category = categoryRepository.findById(classroomCreateDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
        User user = userRepository.findById(classroomCreateDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("강사를 찾을 수 없습니다."));

        // 강의 생성
        Classroom classroom = Classroom.builder()
                .category(category)
                .user(user)
                .name(classroomCreateDto.getName())
                .object(classroomCreateDto.getObject())
                .description(classroomCreateDto.getDescription())
                .instructorInfo(classroomCreateDto.getInstructorInfo())
                .prerequisite(classroomCreateDto.getPrerequisite())
                .announcement(classroomCreateDto.getAnnouncement())
                .bannerImagePath(classroomCreateDto.getBannerImagePath())
                .isActive(classroomCreateDto.getIsActive())
                .build();

        classroomRepository.save(classroom);

        // 생성된 강의 정보를 반환
        return ClassroomResponseDto.builder()
                .id(classroom.getId())
                .categoryId(classroom.getCategory().getId())
                .userId(classroom.getUser().getId())
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

    // 강의실 수정
    public ClassroomResponseDto updateClassroom(Long classId, ClassroomUpdateDto classroomUpdateDto) {
        // 수정할 강의실 찾기
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));

        // 강의 정보 업데이트
        classroom.setName(classroomUpdateDto.getName());
        classroom.setObject(classroomUpdateDto.getObject());
        classroom.setDescription(classroomUpdateDto.getDescription());
        classroom.setInstructorInfo(classroomUpdateDto.getInstructorInfo());
        classroom.setPrerequisite(classroomUpdateDto.getPrerequisite());
        classroom.setAnnouncement(classroomUpdateDto.getAnnouncement());
        classroom.setBannerImagePath(classroomUpdateDto.getBannerImagePath());
        classroom.setIsActive(classroomUpdateDto.getIsActive());

        classroomRepository.save(classroom);

        // 수정된 강의 정보 반환
        return ClassroomResponseDto.builder()
                .id(classroom.getId())
                .categoryId(classroom.getCategory().getId())
                .userId(classroom.getUser().getId())
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

    // 강의실 조회
    public ClassroomResponseDto getClassroomById(Long classId) {
        // 개별 강의 조회
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));

        return ClassroomResponseDto.builder()
                .id(classroom.getId())
                .categoryId(classroom.getCategory().getId())
                .userId(classroom.getUser().getId())
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
        return classroomRepository.findAll().stream()
                .map(classroom -> ClassroomResponseDto.builder()
                        .id(classroom.getId())
                        .categoryId(classroom.getCategory().getId())
                        .userId(classroom.getUser().getId())
                        .name(classroom.getName())
                        .object(classroom.getObject())
                        .description(classroom.getDescription())
                        .instructorInfo(classroom.getInstructorInfo())
                        .prerequisite(classroom.getPrerequisite())
                        .announcement(classroom.getAnnouncement())
                        .bannerImagePath(classroom.getBannerImagePath())
                        .isActive(classroom.getIsActive())
                        .build())
                .collect(Collectors.toList());
    }

    // Soft Delete 방식으로 강의실 삭제
    public void deleteClassroom(Long classId) {
        // 삭제할 강의실 찾기
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));

        // Soft Delete 처리: deletedAt 필드에 현재 시간 저장
        classroom.setDeletedAt(LocalDateTime.now());
        classroomRepository.save(classroom); // 실제로 삭제하지 않고 상태만 업데이트
    }
}
