package com.nemo.oceanAcademy.domain.participant.application.dto;
import com.nemo.oceanAcademy.domain.review.dataAccess.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipantResponseDto {
    private String id;
    private String email;
    private String nickname;
    private String profileImagePath;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private List<Review> reviews;
}
