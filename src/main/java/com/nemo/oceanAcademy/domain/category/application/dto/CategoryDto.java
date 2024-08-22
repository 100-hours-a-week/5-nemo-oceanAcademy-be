package com.nemo.oceanAcademy.domain.category.application.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {

    //PK 카테고리 아이디, 식별자
    private Integer id;
    //카테고리 이름
    private String name;
}
