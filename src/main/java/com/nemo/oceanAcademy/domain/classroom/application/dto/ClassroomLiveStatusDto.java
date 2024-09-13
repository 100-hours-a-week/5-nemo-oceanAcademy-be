package com.nemo.oceanAcademy.domain.classroom.application.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassroomLiveStatusDto {
    private boolean isActive;
}
