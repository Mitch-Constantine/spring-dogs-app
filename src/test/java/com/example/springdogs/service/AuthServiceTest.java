package com.example.springdogs.service;

import com.example.springdogs.dto.LoginRequest;
import com.example.springdogs.dto.LoginResponse;
import com.example.springdogs.dto.UserDto;
import com.example.springdogs.model.User;
import com.example.springdogs.repository.UserRepository;
import com.example.springdogs.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest validLoginRequest;
    private LoginRequest invalidLoginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$encodedPassword");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(User.Role.GUEST);
        testUser.setActive(true);

        validLoginRequest = new LoginRequest();
        validLoginRequest.setUsername("testuser");
        validLoginRequest.setPassword("plainPassword");

        invalidLoginRequest = new LoginRequest();
        invalidLoginRequest.setUsername("testuser");
        invalidLoginRequest.setPassword("wrongPassword");
    }

    @Test
    void login_WithValidCredentials_ReturnsLoginResponse() {
        // Arrange
        String expectedToken = "jwt-token-123";
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("plainPassword", "$2a$10$encodedPassword")).thenReturn(true);
        when(jwtUtils.generateToken("testuser")).thenReturn(expectedToken);

        // Act
        LoginResponse response = authService.login(validLoginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(expectedToken, response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertNotNull(response.getUser());
        assertEquals("testuser", response.getUser().getUsername());
        assertEquals("test@example.com", response.getUser().getEmail());

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("plainPassword", "$2a$10$encodedPassword");
        verify(jwtUtils).generateToken("testuser");
    }

    @Test
    void login_WithInvalidUsername_ThrowsRuntimeException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        validLoginRequest.setUsername("nonexistent");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.login(validLoginRequest));
        assertEquals("Invalid credentials", exception.getMessage());

        verify(userRepository).findByUsername("nonexistent");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtils, never()).generateToken(anyString());
    }

    @Test
    void login_WithInvalidPassword_ThrowsRuntimeException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "$2a$10$encodedPassword")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.login(invalidLoginRequest));
        assertEquals("Invalid credentials", exception.getMessage());

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("wrongPassword", "$2a$10$encodedPassword");
        verify(jwtUtils, never()).generateToken(anyString());
    }

    @Test
    void login_WithInactiveUser_ThrowsRuntimeException() {
        // Arrange
        testUser.setActive(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("plainPassword", "$2a$10$encodedPassword")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.login(validLoginRequest));
        assertEquals("User account is deactivated", exception.getMessage());

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("plainPassword", "$2a$10$encodedPassword");
        verify(jwtUtils, never()).generateToken(anyString());
    }

    @Test
    void login_WithNullUserFromRepository_ThrowsRuntimeException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.login(validLoginRequest));
        assertEquals("Invalid credentials", exception.getMessage());

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtils, never()).generateToken(anyString());
    }

    @Test
    void login_WithAdminUser_ReturnsCorrectRoleInUserDto() {
        // Arrange
        testUser.setRole(User.Role.ADMIN);
        String expectedToken = "jwt-token-123";
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("plainPassword", "$2a$10$encodedPassword")).thenReturn(true);
        when(jwtUtils.generateToken("testuser")).thenReturn(expectedToken);

        // Act
        LoginResponse response = authService.login(validLoginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(User.Role.ADMIN, response.getUser().getRole());
        assertEquals(expectedToken, response.getAccessToken());
    }

    @Test
    void login_WithGuestUser_ReturnsCorrectRoleInUserDto() {
        // Arrange
        testUser.setRole(User.Role.GUEST);
        String expectedToken = "jwt-token-123";
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("plainPassword", "$2a$10$encodedPassword")).thenReturn(true);
        when(jwtUtils.generateToken("testuser")).thenReturn(expectedToken);

        // Act
        LoginResponse response = authService.login(validLoginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(User.Role.GUEST, response.getUser().getRole());
        assertEquals(expectedToken, response.getAccessToken());
    }
}
