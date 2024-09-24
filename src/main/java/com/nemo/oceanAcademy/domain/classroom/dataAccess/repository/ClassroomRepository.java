package com.nemo.oceanAcademy.domain.classroom.dataAccess.repository;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomResponseDto;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.entity.Classroom;
import org.springframework.data.domain.Pageable;  // 이 부분을 수정했습니다
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    boolean existsById(Long id);

    // 라이브 강의 조회
    @Query("SELECT new com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomResponseDto(c) " +
            "FROM Classroom c WHERE c.isActive = true " +
            "AND (:categoryId IS NULL OR c.category.id = :categoryId)")
    List<ClassroomResponseDto> findLiveClassrooms(@Param("categoryId") Integer categoryId, Pageable pageable);

    // 수강 중인 강의 조회
    @Query("SELECT new com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomResponseDto(c) " +
            "FROM Classroom c JOIN c.participants p WHERE p.user.id = :userId " +
            "AND (:categoryId IS NULL OR c.category.id = :categoryId)")
    List<ClassroomResponseDto> findEnrolledClassrooms(@Param("categoryId") Integer categoryId, @Param("userId") String userId, Pageable pageable);

    // 카테고리 필터링만 적용
    @Query("SELECT new com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomResponseDto(c) " +
            "FROM Classroom c WHERE c.category.id = :categoryId")
    List<ClassroomResponseDto> findClassroomsByCategoryId(@Param("categoryId") Integer categoryId, Pageable pageable);

    // 내가 개설한 강의 조회
    @Query("SELECT new com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomResponseDto(c, COUNT(p)) " +
            "FROM Classroom c " +
            "LEFT JOIN Participant p ON c.id = p.classroom.id " +
            "WHERE c.user.id = :userId " +
            "AND (:categoryId IS NULL OR c.category.id = :categoryId) " +
            "GROUP BY c.id")
    List<ClassroomResponseDto> findCreatedClassrooms(@Param("categoryId") Integer categoryId, @Param("userId") String userId, Pageable pageable);

    // 상위 10개 강의 조회
    /* 현재는 rating 없음
        @Query("SELECT new com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomResponseDto(c) " +
                "FROM Classroom c " +
                "WHERE (:categoryId IS NULL OR c.category.id = :categoryId) " +
                "ORDER BY c.rating DESC")
        List<ClassroomResponseDto> findTopTenClassrooms(@Param("categoryId") Integer categoryId, Pageable pageable);
     */

    // 전체 강의 조회
    @Query("SELECT new com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomResponseDto(c) " +
            "FROM Classroom c " +
            "WHERE (:categoryId IS NULL OR c.category.id = :categoryId)")
    List<ClassroomResponseDto> findAllClassrooms(@Param("categoryId") Integer categoryId, Pageable pageable);

    // 전체 강의실 조회
    @Query("SELECT c FROM Classroom c " +
            "JOIN FETCH c.user u " +
            "JOIN FETCH c.category cat")
    List<Classroom> findAllWithJoins();

    // 단일 강의실 조회
    @Query("SELECT c FROM Classroom c " +
            "JOIN FETCH c.user u " +
            "JOIN FETCH c.category cat " +
            "WHERE c.id = :classId")
    Optional<Classroom> findByIdWithJoins(@Param("classId") Long classId);


}
