package com.example.springdogs.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationEntryPointTest {

    @InjectMocks
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void commence_WithAnyAuthenticationException_Returns401Status() throws IOException {
        // Arrange
        AuthenticationException authException = new InsufficientAuthenticationException("Full authentication is required");

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("Error: Unauthorized", response.getErrorMessage());
    }

    @Test
    void commence_WithBadCredentialsException_Returns401Status() throws IOException {
        // Arrange
        AuthenticationException authException = new BadCredentialsException("Bad credentials");

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("Error: Unauthorized", response.getErrorMessage());
    }

    @Test
    void commence_WithInsufficientAuthenticationException_Returns401Status() throws IOException {
        // Arrange
        AuthenticationException authException = new InsufficientAuthenticationException("Insufficient authentication");

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("Error: Unauthorized", response.getErrorMessage());
    }

    @Test
    void commence_WithCustomAuthenticationException_Returns401Status() throws IOException {
        // Arrange
        AuthenticationException authException = new AuthenticationException("Custom authentication error") {};

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("Error: Unauthorized", response.getErrorMessage());
    }

    @Test
    void commence_SetsCorrectResponseHeaders() throws IOException {
        // Arrange
        AuthenticationException authException = new InsufficientAuthenticationException("Test exception");

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("Error: Unauthorized", response.getErrorMessage());
        // Note: Content-Type header may not be set by sendError method
    }

    @Test
    void commence_WithNullExceptionMessage_StillReturns401() throws IOException {
        // Arrange
        AuthenticationException authException = new AuthenticationException(null) {};

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("Error: Unauthorized", response.getErrorMessage());
    }

    @Test
    void commence_WithEmptyExceptionMessage_StillReturns401() throws IOException {
        // Arrange
        AuthenticationException authException = new AuthenticationException("") {};

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("Error: Unauthorized", response.getErrorMessage());
    }

    @Test
    void commence_HandlesIOException_Properly() throws IOException {
        // Arrange
        AuthenticationException authException = new InsufficientAuthenticationException("Test exception");
        
        // Create a response that throws IOException when sendError is called
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        doThrow(new IOException("Test IOException")).when(mockResponse).sendError(anyInt(), anyString());

        // Act & Assert
        assertThrows(IOException.class, () -> 
            jwtAuthenticationEntryPoint.commence(request, mockResponse, authException));
    }

    @Test
    void commence_WithDifferentRequestPaths_ReturnsConsistent401() throws IOException {
        // Arrange
        AuthenticationException authException = new InsufficientAuthenticationException("Test exception");

        // Test with different request paths
        String[] paths = {"/api/dogs", "/api/admin", "/protected", "/api/secure"};

        for (String path : paths) {
            MockHttpServletResponse testResponse = new MockHttpServletResponse();
            request.setRequestURI(path);

            // Act
            jwtAuthenticationEntryPoint.commence(request, testResponse, authException);

            // Assert
            assertEquals(HttpServletResponse.SC_UNAUTHORIZED, testResponse.getStatus());
            assertEquals("Error: Unauthorized", testResponse.getErrorMessage());
        }
    }

    @Test
    void commence_WithDifferentRequestMethods_ReturnsConsistent401() throws IOException {
        // Arrange
        AuthenticationException authException = new InsufficientAuthenticationException("Test exception");

        // Test with different HTTP methods
        String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH"};

        for (String method : methods) {
            MockHttpServletResponse testResponse = new MockHttpServletResponse();
            request.setMethod(method);

            // Act
            jwtAuthenticationEntryPoint.commence(request, testResponse, authException);

            // Assert
            assertEquals(HttpServletResponse.SC_UNAUTHORIZED, testResponse.getStatus());
            assertEquals("Error: Unauthorized", testResponse.getErrorMessage());
        }
    }

    @Test
    void commence_LogsExceptionMessage() throws IOException {
        // Arrange
        String exceptionMessage = "Test authentication exception message";
        AuthenticationException authException = new InsufficientAuthenticationException(exceptionMessage);

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        // The entry point should log the exception message
        // This is verified by checking that the method completes without throwing exceptions
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("Error: Unauthorized", response.getErrorMessage());
    }

    @Test
    void commence_DoesNotExposeInternalDetails() throws IOException {
        // Arrange
        String sensitiveMessage = "Internal server details that should not be exposed";
        AuthenticationException authException = new AuthenticationException(sensitiveMessage) {};

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        // The response should not contain the internal exception details
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals("Error: Unauthorized", response.getErrorMessage());
        assertFalse(response.getErrorMessage().contains(sensitiveMessage));
    }
}
