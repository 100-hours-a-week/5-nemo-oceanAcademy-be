package com.nemo.oceanAcademy.domain.auth.controller;

import com.nemo.oceanAcademy.domain.user.entity.User;
import com.nemo.oceanAcademy.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void testCheckSignupStatus() throws Exception {
        when(userService.getCurrentUserId()).thenReturn(1L);
        when(userService.isUserSignedUp(1L)).thenReturn(true);

        mockMvc.perform(get("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(userService, times(1)).getCurrentUserId();
        verify(userService, times(1)).isUserSignedUp(1L);
    }

    @Test
    public void testSignup() throws Exception {
        AuthDto authDto = new AuthDto();
        authDto.setOauthId(1L);
        authDto.setNickname("testNickname");
        authDto.setEmail("test@example.com");
        authDto.setProfileImagePath("/path/to/image");

        String jsonRequest = new ObjectMapper().writeValueAsString(authDto);

        when(jwtProvider.createToken(1L)).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("jwt-token"));

        verify(userService, times(1)).createUser(any(User.class));
        verify(jwtProvider, times(1)).createToken(1L);
    }

    @Test
    public void testLogout() throws Exception {
        when(userService.getCurrentUserId()).thenReturn(1L);

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).getCurrentUserId();
        verify(jwtProvider, times(1)).invalidateToken(1L);
    }
}
