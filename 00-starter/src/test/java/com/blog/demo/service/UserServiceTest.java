package com.blog.demo.service;

import com.blog.demo.dto.UserRequest;
import com.blog.demo.dto.UserResponse;
import com.blog.demo.entity.Role;
import com.blog.demo.entity.User;
import com.blog.demo.exception.GlobalException;
import com.blog.demo.repository.UserRepository;
import com.blog.demo.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testFindByIdSuccess() {
        User user = new User();
        user.setId(1L);
        user.setUsername("ouda");
        user.setRole(Role.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse response = userService.findById(1);

        assertNotNull(response);
        assertEquals("ouda", response.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(GlobalException.class, () -> userService.findById(999));
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void testRegisterDuplicateUsernameThrowsException() {
        UserRequest request = new UserRequest();
        request.setUsername("existingUser");
        request.setPassword("password123");

        User userEntity = new User();
        userEntity.setUsername("existingUser");
        userEntity.setPassword("password123");

        when(objectMapper.convertValue(request, User.class)).thenReturn(userEntity);
        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new User()));

        assertThrows(GlobalException.class, () -> userService.register(request));
        verify(userRepository, never()).save(any());
    }
}
