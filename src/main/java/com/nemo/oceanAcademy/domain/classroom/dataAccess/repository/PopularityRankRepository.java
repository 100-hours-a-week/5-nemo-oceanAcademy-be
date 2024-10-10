package com.nemo.oceanAcademy.domain.classroom.dataAccess.repository;

import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomResponseDto;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.entity.Classroom;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.entity.PopularityRank;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopularityRankRepository extends JpaRepository<PopularityRank, Long> {
    // 인기 순위를 조회하는 메서드
    List<PopularityRank> findAllByOrderByRankingAsc(Pageable pageable);

    List<PopularityRank> findTop10ByRankingBetweenOrderByRankingAsc(int start, int end);

    List<PopularityRank> findTopByRankingBetween(int start, int end);

//    @Query("SELECT new com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomResponseDto(pr.classroom) " +
//            "FROM PopularityRank pr " +
//            "WHERE pr.ranking IS NOT NULL " +
//            "ORDER BY pr.ranking ASC")
    @Query("SELECT new com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomResponseDto(pr.classroom) " +
            "FROM PopularityRank pr WHERE pr.ranking >= 1 AND pr.ranking <= 10 ORDER BY pr.ranking ASC")
    List<ClassroomResponseDto> findTopRankedClassrooms();

    PopularityRank findByClassroom(Classroom classroom);
}

