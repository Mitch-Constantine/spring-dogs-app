package com.example.springdogs.controller;

import com.example.springdogs.dto.LoginRequest;
import com.example.springdogs.dto.LoginResponse;
import com.example.springdogs.dto.UserDto;
import com.example.springdogs.model.User;
import com.example.springdogs.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest validLoginRequest;
    private LoginResponse validLoginResponse;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setUsername("testuser");
        testUserDto.setEmail("test@example.com");
        testUserDto.setFirstName("Test");
        testUserDto.setLastName("User");
        testUserDto.setRole(User.Role.GUEST);

        validLoginRequest = new LoginRequest();
        validLoginRequest.setUsername("testuser");
        validLoginRequest.setPassword("password123");

        validLoginResponse = new LoginResponse();
        validLoginResponse.setAccessToken("jwt-token-123");
        validLoginResponse.setTokenType("Bearer");
        validLoginResponse.setUser(testUserDto);
    }

    @Test
    void login_WithValidCredentials_ReturnsLoginResponse() {
        // Arrange
        when(authService.login(any(LoginRequest.class))).thenReturn(validLoginResponse);

        // Act
        ResponseEntity<LoginResponse> response = authController.login(validLoginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("jwt-token-123", response.getBody().getAccessToken());
        assertEquals("Bearer", response.getBody().getTokenType());
        assertEquals("testuser", response.getBody().getUser().getUsername());
        assertEquals("test@example.com", response.getBody().getUser().getEmail());
    }

    @Test
    void login_WithInvalidCredentials_ThrowsRuntimeException() {
        // Arrange
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authController.login(validLoginRequest));
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void login_WithInactiveUser_ThrowsRuntimeException() {
        // Arrange
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("User account is deactivated"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authController.login(validLoginRequest));
        assertEquals("User account is deactivated", exception.getMessage());
    }

    @Test
    void logout_ReturnsSuccessMessage() {
        // Act
        ResponseEntity<?> response = authController.logout();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Logout successful"));
    }

    @Test
    void login_WithAdminUser_ReturnsCorrectRole() {
        // Arrange
        testUserDto.setRole(User.Role.ADMIN);
        validLoginResponse.setUser(testUserDto);
        when(authService.login(any(LoginRequest.class))).thenReturn(validLoginResponse);

        // Act
        ResponseEntity<LoginResponse> response = authController.login(validLoginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(User.Role.ADMIN, response.getBody().getUser().getRole());
    }

    @Test
    void login_WithGuestUser_ReturnsCorrectRole() {
        // Arrange
        when(authService.login(any(LoginRequest.class))).thenReturn(validLoginResponse);

        // Act
        ResponseEntity<LoginResponse> response = authController.login(validLoginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(User.Role.GUEST, response.getBody().getUser().getRole());
    }
}
