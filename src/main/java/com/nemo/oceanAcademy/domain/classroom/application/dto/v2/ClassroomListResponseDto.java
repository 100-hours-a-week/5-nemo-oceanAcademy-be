package com.nemo.oceanAcademy.domain.classroom.application.dto.v2;


import com.nemo.oceanAcademy.domain.classroom.dataAccess.entity.Classroom;
import jakarta.persistence.JoinColumn;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassroomListResponseDto {

    // PK 강의실 아이디 (조회 시 반환)
    private Long id;

    // 강사 닉네임 (조회 시 반환)
    @JoinColumn(name = "instructor_info")
    private String instructor;

    // 카테고리 이름 (조회 시 반환)
    @JoinColumn(name = "name")
    private String category;

    // 강의 평점
    private Double rating;

    // 강의 이름 (조회 시 반환)
    private String name;

    // 강의 배너 이미지 경로 (조회 시 반환)
    private String bannerImagePath;

    // 강의실 활성화 여부 (조회 시 반환)
    private Boolean isActive;

    // Classroom 엔티티를 기반으로 생성하는 생성자 추가
    public ClassroomListResponseDto(Classroom classroom, Double averageRating) {
        this.id = classroom.getId();  // 강의실 아이디
        this.instructor = classroom.getUser().getNickname();  // 강사 이름
        this.category = classroom.getCategory().getName();  // 카테고리 이름
        this.rating = averageRating;
        this.name = classroom.getName();  // 강의 이름
        this.bannerImagePath = classroom.getBannerImagePath();  // 배너 이미지 경로
        this.isActive = classroom.getIsActive();  // 강의실 활성화 여부
    }
}
