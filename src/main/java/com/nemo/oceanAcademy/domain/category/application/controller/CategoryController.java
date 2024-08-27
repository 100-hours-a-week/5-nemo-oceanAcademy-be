package com.nemo.oceanAcademy.domain.category.application.controller;

import com.nemo.oceanAcademy.domain.category.application.dto.CategoryDto;
import com.nemo.oceanAcademy.domain.category.application.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * CategoryController는 카테고리와 관련된 API 요청을 처리합니다.
 * 전체 카테고리 목록 조회 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 전체 카테고리 리스트 조회
     *
     * @return ResponseEntity<List<CategoryDto>> 전체 카테고리 목록
     */
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}
