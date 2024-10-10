package com.nemo.oceanAcademy.domain.classroom.dataAccess.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "popularity_ranks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PopularityRank {
    // PK, 인기 순위 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK, 강의실 ID (일대일 관계)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "class_id", nullable = false, unique = true)
    private Classroom classroom;

    // 점수
    @Column(nullable = false)
    private Double score;

    // 랭킹
    @Column(nullable = true)
    private Integer ranking;
}
