package com.nemo.oceanAcademy.domain.category.entity;

import com.nemo.oceanAcademy.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {

    //PK 카테고리 아이디, 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //카테고리 이름
    @Column(length = 20, nullable = false)
    private String name;
}
