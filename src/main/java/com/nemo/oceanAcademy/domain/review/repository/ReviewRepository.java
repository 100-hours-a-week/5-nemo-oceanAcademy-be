package com.nemo.oceanAcademy.domain.review.repository;

import com.nemo.oceanAcademy.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
