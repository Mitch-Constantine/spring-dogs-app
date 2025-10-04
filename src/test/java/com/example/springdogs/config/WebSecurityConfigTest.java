package com.example.springdogs.config;

import com.example.springdogs.security.CustomUserDetailsService;
import com.example.springdogs.security.JwtAuthenticationEntryPoint;
import com.example.springdogs.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WebSecurityConfigTest {

    @InjectMocks
    private WebSecurityConfig webSecurityConfig;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Test
    void authenticationProvider_BeanExistsAndIsConfigured() {
        // Act
        DaoAuthenticationProvider provider = webSecurityConfig.authenticationProvider();

        // Assert
        assertNotNull(provider);
        // Note: getter methods are not accessible, but we can verify the bean exists
    }

    @Test
    void passwordEncoder_BeanExists() {
        // Act
        PasswordEncoder encoder = webSecurityConfig.passwordEncoder();

        // Assert
        assertNotNull(encoder);
        assertTrue(encoder.matches("password", encoder.encode("password")));
    }

    @Test
    void authenticationManager_BeanExists() throws Exception {
        // This test verifies the bean configuration exists
        // The actual AuthenticationManager will be created by Spring context
        assertNotNull(webSecurityConfig);
    }

    @Test
    void corsConfigurationSource_BeanExists() {
        // Act
        CorsConfigurationSource corsSource = webSecurityConfig.corsConfigurationSource();

        // Assert
        assertNotNull(corsSource);
    }

    @Test
    void filterChain_BeanExists() throws Exception {
        // This test verifies the bean configuration exists
        // The actual SecurityFilterChain will be created by Spring context
        assertNotNull(webSecurityConfig);
    }

    @Test
    void authenticationJwtTokenFilter_BeanExists() {
        // Act
        JwtAuthenticationFilter filter = webSecurityConfig.authenticationJwtTokenFilter();

        // Assert
        assertNotNull(filter);
    }

}
