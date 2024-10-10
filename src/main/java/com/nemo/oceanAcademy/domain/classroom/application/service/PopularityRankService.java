package com.nemo.oceanAcademy.domain.classroom.application.service;

import com.nemo.oceanAcademy.domain.classroom.dataAccess.entity.Classroom;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.repository.ClassroomRepository;
import com.nemo.oceanAcademy.domain.participant.dataAccess.repository.ParticipantRepository;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.entity.PopularityRank;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.repository.PopularityRankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class PopularityRankService {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private PopularityRankRepository popularityRankRepository;

    public void calculatePopularityRanks() {
        List<Classroom> classrooms = classroomRepository.findAll();

        for (Classroom classroom : classrooms) {
            long participantCount = participantRepository.countByClassroom(classroom);
            LocalDateTime createdAt = classroom.getCreatedAt();
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
            Duration duration = Duration.between(createdAt, now);
            double timeDifference = duration.toHours(); // 시간 단위의 차이로 변환

            // ArithmeticException 방지
            if (timeDifference == 0) {
                timeDifference = 1; // 최소 1시간으로 설정
            }

            double score = participantCount / Math.pow(timeDifference, 1.8);

            // PopularityRank 조회 (아이디를 이용하여)
            PopularityRank existingRank = popularityRankRepository.findByClassroom(classroom);

            if (existingRank != null) {
                // 기존 레코드가 존재하는 경우, 점수 업데이트
                existingRank.setScore(score);
            } else {
                // 기존 레코드가 없는 경우, 새 레코드 생성
                existingRank = new PopularityRank();
                existingRank.setClassroom(classroom);
                existingRank.setScore(score);
            }

            popularityRankRepository.save(existingRank); // 레코드 저장
        }

        updateRankings();
    }

    private void updateRankings() {
        List<PopularityRank> ranks = popularityRankRepository.findAll();
        ranks.sort((a, b) -> Double.compare(b.getScore(), a.getScore())); // score 기준 내림차순 정렬

        for (int i = 0; i < ranks.size(); i++) {
            ranks.get(i).setRanking(i + 1);
        }

        popularityRankRepository.saveAll(ranks);
    }
}

