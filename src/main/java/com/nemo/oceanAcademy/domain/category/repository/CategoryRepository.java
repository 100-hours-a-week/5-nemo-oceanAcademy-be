package com.nemo.oceanAcademy.domain.category.repository;

import com.nemo.oceanAcademy.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
