package com.example.springdogs.security;

import com.example.springdogs.model.User;
import com.example.springdogs.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private UserDetails userDetails;
    private User testUser;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(User.Role.GUEST);
        testUser.setActive(true);

        userDetails = CustomUserDetailsService.CustomUserPrincipal.create(testUser);
        
        // Clear security context before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WithValidBearerToken_SetsSecurityContext() throws ServletException, IOException {
        // Arrange
        String validToken = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + validToken);
        
        when(jwtUtils.getUsernameFromToken(validToken)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtils.validateToken(validToken, userDetails)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getName());
        assertTrue(SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken);
    }

    @Test
    void doFilterInternal_WithValidToken_CreatesAuthenticationWithAuthorities() throws ServletException, IOException {
        // Arrange
        String validToken = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + validToken);
        
        when(jwtUtils.getUsernameFromToken(validToken)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtils.validateToken(validToken, userDetails)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        UsernamePasswordAuthenticationToken auth = 
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth.getAuthorities());
        assertFalse(auth.getAuthorities().isEmpty());
    }

    @Test
    void doFilterInternal_WithoutAuthorizationHeader_DoesNotSetSecurityContext() throws ServletException, IOException {
        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtils, never()).validateToken(anyString(), any(UserDetails.class));
    }

    @Test
    void doFilterInternal_WithMalformedAuthorizationHeader_DoesNotSetSecurityContext() throws ServletException, IOException {
        // Arrange
        request.addHeader("Authorization", "InvalidFormat token");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtils, never()).validateToken(anyString(), any(UserDetails.class));
    }

    @Test
    void doFilterInternal_WithInvalidToken_DoesNotSetSecurityContext() throws ServletException, IOException {
        // Arrange
        String invalidToken = "invalid.jwt.token";
        request.addHeader("Authorization", "Bearer " + invalidToken);
        
        when(jwtUtils.getUsernameFromToken(invalidToken)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtils.validateToken(invalidToken, userDetails)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithExpiredToken_DoesNotSetSecurityContext() throws ServletException, IOException {
        // Arrange
        String expiredToken = "expired.jwt.token";
        request.addHeader("Authorization", "Bearer " + expiredToken);
        
        when(jwtUtils.getUsernameFromToken(expiredToken)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtils.validateToken(expiredToken, userDetails)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithTokenParsingException_ContinuesChain() throws ServletException, IOException {
        // Arrange
        String malformedToken = "malformed.token";
        request.addHeader("Authorization", "Bearer " + malformedToken);
        
        when(jwtUtils.getUsernameFromToken(malformedToken)).thenThrow(new RuntimeException("Invalid token"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithUserNotFoundException_ContinuesChain() throws ServletException, IOException {
        // Arrange
        String validToken = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + validToken);
        
        when(jwtUtils.getUsernameFromToken(validToken)).thenReturn("nonexistent");
        when(userDetailsService.loadUserByUsername("nonexistent"))
                .thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithEmptyAuthorizationHeader_DoesNotSetSecurityContext() throws ServletException, IOException {
        // Arrange
        request.addHeader("Authorization", "");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtils, never()).validateToken(anyString(), any(UserDetails.class));
    }

    @Test
    void doFilterInternal_WithBearerPrefixButNoToken_DoesNotSetSecurityContext() throws ServletException, IOException {
        // Arrange
        request.addHeader("Authorization", "Bearer ");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtils, never()).validateToken(anyString(), any(UserDetails.class));
    }

    @Test
    void doFilterInternal_WithValidToken_SetsWebAuthenticationDetails() throws ServletException, IOException {
        // Arrange
        String validToken = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + validToken);
        
        when(jwtUtils.getUsernameFromToken(validToken)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtils.validateToken(validToken, userDetails)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        UsernamePasswordAuthenticationToken auth = 
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth.getDetails());
    }

    @Test
    void doFilterInternal_WithValidBearerTokenFormat_ProcessesCorrectly() throws ServletException, IOException {
        // Arrange
        String validToken = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + validToken);
        
        when(jwtUtils.getUsernameFromToken(validToken)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtils.validateToken(validToken, userDetails)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithInvalidAuthorizationPrefix_DoesNotProcess() throws ServletException, IOException {
        // Arrange
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtils, never()).validateToken(anyString(), any(UserDetails.class));
    }

    @Test
    void doFilterInternal_WithBearerPrefixOnly_DoesNotProcess() throws ServletException, IOException {
        // Arrange
        request.addHeader("Authorization", "Bearer");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtils, never()).validateToken(anyString(), any(UserDetails.class));
    }

    @Test
    void doFilterInternal_WithBearerAndSpaceOnly_DoesNotProcess() throws ServletException, IOException {
        // Arrange
        request.addHeader("Authorization", "Bearer ");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtils, never()).validateToken(anyString(), any(UserDetails.class));
    }
}
