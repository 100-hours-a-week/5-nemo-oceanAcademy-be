package com.nemo.oceanAcademy.domain.category.application.service;
import com.nemo.oceanAcademy.domain.category.dataAccess.entity.Category;
import com.nemo.oceanAcademy.domain.category.dataAccess.repository.CategoryRepository;
import com.nemo.oceanAcademy.domain.category.application.dto.CategoryDto;
import io.sentry.Sentry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CategoryService는 카테고리 관련 비즈니스 로직을 처리합니다.
 * 카테고리 조회 기능을 담당합니다.
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * 전체 카테고리 목록을 조회합니다.
     *
     * @return List<CategoryDto> 전체 카테고리 목록
     */
    public List<CategoryDto> getAllCategories() {
        try {
            // DB에서 모든 카테고리를 조회
            List<Category> categories = categoryRepository.findAll();

            // 엔티티를 DTO로 변환하여 반환
            return categories.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("카테고리 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 카테고리 엔티티를 CategoryDto로 변환합니다.
     *
     * @param category 변환할 카테고리 엔티티
     * @return CategoryDto 변환된 DTO
     */
    private CategoryDto convertToDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
