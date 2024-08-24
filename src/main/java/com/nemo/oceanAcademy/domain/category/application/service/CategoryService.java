package com.nemo.oceanAcademy.domain.category.application.service;

import com.nemo.oceanAcademy.domain.category.dataAccess.entity.Category;
import com.nemo.oceanAcademy.domain.category.dataAccess.repository.CategoryRepository;
import com.nemo.oceanAcademy.domain.category.application.dto.CategoryDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // 전체 카테고리 조회
    public List<CategoryDto> getAllCategories() {
        // DB에서 모든 카테고리 조회
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 엔티티를 DTO로 변환
    private CategoryDto convertToDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
