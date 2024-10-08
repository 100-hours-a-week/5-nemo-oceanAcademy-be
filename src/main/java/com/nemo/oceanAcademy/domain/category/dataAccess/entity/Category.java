package com.nemo.oceanAcademy.domain.category.dataAccess.entity;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.entity.Classroom;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {

    //PK 카테고리 아이디, 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //카테고리 이름
    @Column(nullable = false, length = 100)
    @Size(min = 1, max = 10, message = "Category name must be between 1 and 10 characters")
    @NotNull(message = "Category name must not be null")
    private String name;

    //양방향 관계 = categories과 연관된 테이블 1개
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Classroom> classrooms;
}
