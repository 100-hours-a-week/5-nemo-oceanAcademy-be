package com.nemo.oceanAcademy.domain.user.application.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    private String nickname;            // 선택적 필드
    private String email;               // 선택적 필드
}
