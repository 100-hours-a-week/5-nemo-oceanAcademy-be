package com.nemo.oceanAcademy.domain.category.application.controller;
import com.nemo.oceanAcademy.domain.category.application.dto.CategoryDto;
import com.nemo.oceanAcademy.domain.category.application.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/*
    /api/categories
        Get - 카테고리 테이블 전체 리스트 불러오기
*/

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 전체 카테고리 리스트 조회
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}
