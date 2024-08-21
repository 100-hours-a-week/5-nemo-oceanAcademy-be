package com.nemo.oceanAcademy.domain.category.dataAccess.repository;

import com.nemo.oceanAcademy.domain.category.dataAccess.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
