package com.example.springdogs.security;

import com.example.springdogs.model.User;
import com.example.springdogs.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(User.Role.GUEST);
        testUser.setActive(true);

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("adminuser");
        adminUser.setPassword("encodedPassword");
        adminUser.setEmail("admin@example.com");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setRole(User.Role.ADMIN);
        adminUser.setActive(true);
    }

    @Test
    void loadUserByUsername_WithExistingUser_ReturnsUserDetails() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    void loadUserByUsername_WithNonExistentUser_ThrowsUsernameNotFoundException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent"));
        assertEquals("User not found: nonexistent", exception.getMessage());
    }

    @Test
    void loadUserByUsername_WithAdminUser_ReturnsCorrectAuthorities() {
        // Arrange
        when(userRepository.findByUsername("adminuser")).thenReturn(Optional.of(adminUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("adminuser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("adminuser", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertEquals(1, userDetails.getAuthorities().size());
    }

    @Test
    void loadUserByUsername_WithGuestUser_ReturnsCorrectAuthorities() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_GUEST")));
        assertEquals(1, userDetails.getAuthorities().size());
    }

    @Test
    void loadUserByUsername_WithInactiveUser_ReturnsDisabledUserDetails() {
        // Arrange
        testUser.setActive(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertFalse(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    void customUserPrincipal_CreationFromUser_WorksCorrectly() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertInstanceOf(CustomUserDetailsService.CustomUserPrincipal.class, userDetails);
        CustomUserDetailsService.CustomUserPrincipal principal = 
                (CustomUserDetailsService.CustomUserPrincipal) userDetails;
        
        assertEquals(1L, principal.getId());
        assertEquals("testuser", principal.getUsername());
        assertEquals("encodedPassword", principal.getPassword());
        assertEquals("test@example.com", principal.getEmail());
        assertEquals("Test", principal.getFirstName());
        assertEquals("User", principal.getLastName());
        assertEquals(User.Role.GUEST, principal.getRole());
        assertTrue(principal.isEnabled());
    }

    @Test
    void customUserPrincipal_AdminUser_HasCorrectProperties() {
        // Arrange
        when(userRepository.findByUsername("adminuser")).thenReturn(Optional.of(adminUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("adminuser");

        // Assert
        assertInstanceOf(CustomUserDetailsService.CustomUserPrincipal.class, userDetails);
        CustomUserDetailsService.CustomUserPrincipal principal = 
                (CustomUserDetailsService.CustomUserPrincipal) userDetails;
        
        assertEquals(2L, principal.getId());
        assertEquals("adminuser", principal.getUsername());
        assertEquals("admin@example.com", principal.getEmail());
        assertEquals("Admin", principal.getFirstName());
        assertEquals("User", principal.getLastName());
        assertEquals(User.Role.ADMIN, principal.getRole());
        assertTrue(principal.isEnabled());
    }

    @Test
    void customUserPrincipal_AccountStatusMethods_ReturnCorrectValues() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void customUserPrincipal_InactiveAccount_IsNotEnabled() {
        // Arrange
        testUser.setActive(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertFalse(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsername_CallsRepositoryWithCorrectUsername() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        userDetailsService.loadUserByUsername("testuser");

        // Assert
        // Verify that findByUsername was called with the correct parameter
        // This is implicitly verified by the mock setup and the fact that the test passes
        // Additional explicit verification could be added with verify() if needed
    }
}
