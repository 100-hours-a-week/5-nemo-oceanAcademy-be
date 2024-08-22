package com.nemo.oceanAcademy.domain.classroom.application.service;

import com.nemo.oceanAcademy.domain.category.dataAccess.entity.Category;
import com.nemo.oceanAcademy.domain.category.dataAccess.repository.CategoryRepository;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomCreateDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomResponseDto;
import com.nemo.oceanAcademy.domain.classroom.application.dto.ClassroomUpdateDto;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.entity.Classroom;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.repository.ClassroomRepository;
import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;
import com.nemo.oceanAcademy.domain.user.dataAccess.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClassroomServiceTest {

    @Mock
    private ClassroomRepository classroomRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ClassroomService classroomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mock 객체 초기화
    }

    @Test
    void createClassroom_Success() {
        // Given
        ClassroomCreateDto classroomCreateDto = new ClassroomCreateDto();
        classroomCreateDto.setName("New Class");
        classroomCreateDto.setCategoryId(100);  // Long 타입으로 수정
        classroomCreateDto.setUserId("100");

        // Mock 객체 준비
        Category category = new Category();  // setId 제거, Mock 객체에서는 수동 설정 불필요
        User user = new User();

        Classroom classroom = Classroom.builder()
                .name(classroomCreateDto.getName())
                .category(category)
                .user(user)
                .build();

        when(categoryRepository.findById(classroomCreateDto.getCategoryId())).thenReturn(Optional.of(category));
        when(userRepository.findById(classroomCreateDto.getUserId())).thenReturn(Optional.of(user));
        when(classroomRepository.save(any(Classroom.class))).thenReturn(classroom);

        // When
        ClassroomResponseDto responseDto = classroomService.createClassroom(classroomCreateDto);

        // Then
        assertNotNull(responseDto);
        assertEquals("New Class", responseDto.getName());
        verify(classroomRepository, times(1)).save(any(Classroom.class));
    }

    @Test
    void updateClassroom_Success() {
        // Given
        Long classId = 1L;
        ClassroomUpdateDto classroomUpdateDto = new ClassroomUpdateDto();
        classroomUpdateDto.setName("Updated Class");

        Classroom existingClassroom = Classroom.builder()
                .id(classId)
                .name("Old Class")
                .build();

        when(classroomRepository.findById(classId)).thenReturn(Optional.of(existingClassroom));

        // When
        ClassroomResponseDto responseDto = classroomService.updateClassroom(classId, classroomUpdateDto);

        // Then
        assertNotNull(responseDto);
        assertEquals("Updated Class", responseDto.getName());
        verify(classroomRepository, times(1)).save(existingClassroom);
    }

    @Test
    void getClassroomById_Success() {
        // Given
        Long classId = 1L;
        Classroom classroom = Classroom.builder()
                .id(classId)
                .name("Test Class")
                .build();

        when(classroomRepository.findById(classId)).thenReturn(Optional.of(classroom));

        // When
        ClassroomResponseDto responseDto = classroomService.getClassroomById(classId);

        // Then
        assertNotNull(responseDto);
        assertEquals("Test Class", responseDto.getName());
    }

    @Test
    void deleteClassroom_Success() {
        // Given
        Long classId = 1L;
        Classroom classroom = Classroom.builder()
                .id(classId)
                .build();

        when(classroomRepository.findById(classId)).thenReturn(Optional.of(classroom));

        // When
        classroomService.deleteClassroom(classId);

        // Then
        assertNotNull(classroom.getDeletedAt());
        verify(classroomRepository, times(1)).save(classroom);
    }
}
