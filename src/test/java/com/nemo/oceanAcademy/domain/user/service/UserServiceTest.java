package com.nemo.oceanAcademy.domain.user.service;

import com.nemo.oceanAcademy.domain.user.entity.User;
import com.nemo.oceanAcademy.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService oAuthService;

    @InjectMocks
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testGetCurrentUserId() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("1");

        Long userId = userService.getCurrentUserId();

        assertEquals(1L, userId);
    }

    @Test
    public void testIsNicknameTaken() {
        when(userRepository.existsByNickname("testNickname")).thenReturn(true);

        boolean isTaken = userService.isNicknameTaken("testNickname");

        assertTrue(isTaken);
    }

    @Test
    public void testIsUserSignedUp() {
        when(userRepository.existsById(1L)).thenReturn(true);

        boolean isSignedUp = userService.isUserSignedUp(1L);

        assertTrue(isSignedUp);
    }

    @Test
    public void testCreateUser() {
        User user = User.builder().nickname("testNickname").build();
        when(userRepository.save(user)).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertEquals("testNickname", createdUser.getNickname());
    }

    @Test
    public void testDeleteCurrentUser() {
        User user = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("1");

        userService.deleteCurrentUser();

        verify(oAuthService, times(1)).logoutUser(1L);
        verify(userRepository, times(1)).save(user);
        assertNotNull(user.getDeletedAt());
    }
}
