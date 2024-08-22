package com.nemo.oceanAcademy.domain.classroom.application.service;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomCreateDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomDashboardDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomUpdateDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomResponseDto;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.entity.Classroom;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.repository.ClassroomRepository;
import com.nemo.oceanAcademy.domain.category.dataAccess.entity.Category;
import com.nemo.oceanAcademy.domain.category.dataAccess.repository.CategoryRepository;
import com.nemo.oceanAcademy.domain.participant.dataAccess.repository.ParticipantRepository;
import com.nemo.oceanAcademy.domain.schedule.application.dto.ScheduleDto;
import com.nemo.oceanAcademy.domain.schedule.dataAccess.repository.ScheduleRepository;
import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;
import com.nemo.oceanAcademy.domain.user.dataAccess.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;

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

    // 강의 필터링 및 페이징 처리
    public List<ClassroomResponseDto> getFilteredClassrooms(String target, Long categoryId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);

        // target에 따른 필터링
        if (target != null) {
            switch (target) {
                case "live":
                    return classroomRepository.findLiveClassrooms(categoryId, pageable);
                case "enrolled":
                    return classroomRepository.findEnrolledClassrooms(categoryId, pageable);
                case "created":
                    return classroomRepository.findCreatedClassrooms(categoryId, pageable);
                /* 수강생 제일 많은 강의
                    case "topten":
                        return classroomRepository.findTopTenClassrooms(categoryId, pageable);
                 */
                case "ALL":  // target이 "ALL"이면 전체 강의실 조회
                    return getAllClassrooms();
                default:
                    return getAllClassrooms();  // 기본값으로 전체 조회
            }
        }

        // target이 없는 경우 전체 강의 조회
        return getAllClassrooms();
    }

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
                .isActive(false)
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

    // Soft Delete 방식으로 강의실 삭제 - soft delete
    public ResponseEntity<?> deleteClassroom(Long classId) {
        // classId로 삭제할 강의실 찾기
        if (classroomRepository.existsById(classId)) {
            Classroom classroom = classroomRepository.findById(classId)
                    .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));
            // deleted_at 컬럼에 삭제 일시 기록
            classroom.setDeletedAt(LocalDateTime.now());
            classroomRepository.save(classroom);
            return ResponseEntity.ok("{\"message\": \"강의삭제 완료\"}");
        } else {
            return ResponseEntity.status(400).body("{\"message\": \"강의삭제 실패\"}");
        }
    }

    // 사용자의 강의실 역할 확인
    public String getUserRoleInClassroom(Long classId, String userId) {
        // 강의실 조회
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));

        // 강사 확인
        if (classroom.getUser().getId().equals(userId)) { return "강사"; }

        // 수강생 확인 (participants 테이블에서 조회)
        boolean isParticipant = participantRepository.existsByClassroomIdAndUserId(classId, userId);
        if (isParticipant) { return "수강생"; }

        return "관계없음";
    }

    // 강의를 듣는 수강생 리스트 조회
    public List<User> getClassroomStudents(Long classId) {
        // 해당 강의실의 수강생 리스트를 participants 테이블에서 가져옵니다.
        return participantRepository.findUsersByClassroomId(classId);
    }

    // 강의실 대시보드 정보 및 스케줄 가져오기
    public ClassroomDashboardDto getClassroomDashboard(Long classId, String userId) {
        // 강의실 조회
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));

        // 사용자의 강의실 역할 확인
        String role = getUserRoleInClassroom(classId, userId);

        // 스케줄 조회
        List<ScheduleDto> schedules = scheduleRepository.findSchedulesByClassroomId(classId);

        // 강의실 대시보드 정보 빌드
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
    }

}
