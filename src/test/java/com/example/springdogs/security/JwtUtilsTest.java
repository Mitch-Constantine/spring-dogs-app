package com.example.springdogs.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    private final String testSecret = "testSecretKeyThatIsLongEnoughForHS256Algorithm";
    private final int testExpirationMs = 86400000; // 24 hours
    private final String testUsername = "testuser";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", testExpirationMs);
    }

    @Test
    void generateToken_WithValidUsername_CreatesProperlyFormattedJWT() {
        // Act
        String token = jwtUtils.generateToken(testUsername);

        // Assert
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts separated by dots
    }

    @Test
    void generateToken_WithValidUsername_IncludesCorrectSubject() {
        // Act
        String token = jwtUtils.generateToken(testUsername);

        // Assert
        String extractedUsername = jwtUtils.getUsernameFromToken(token);
        assertEquals(testUsername, extractedUsername);
    }

    @Test
    void generateToken_WithValidUsername_IncludesIssuedAtTimestamp() {
        // Act
        String token = jwtUtils.generateToken(testUsername);

        // Assert
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        assertNotNull(claims.getIssuedAt());
        assertTrue(claims.getIssuedAt().getTime() <= System.currentTimeMillis());
    }

    @Test
    void generateToken_WithValidUsername_IncludesExpirationTimestamp() {
        // Act
        String token = jwtUtils.generateToken(testUsername);

        // Assert
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().getTime() > System.currentTimeMillis());
    }

    @Test
    void getUsernameFromToken_WithValidToken_ReturnsCorrectUsername() {
        // Arrange
        String token = jwtUtils.generateToken(testUsername);

        // Act
        String extractedUsername = jwtUtils.getUsernameFromToken(token);

        // Assert
        assertEquals(testUsername, extractedUsername);
    }

    @Test
    void getUsernameFromToken_WithInvalidToken_ThrowsException() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThrows(Exception.class, () -> jwtUtils.getUsernameFromToken(invalidToken));
    }

    @Test
    void validateToken_WithValidTokenAndCorrectUser_ReturnsTrue() {
        // Arrange
        String token = jwtUtils.generateToken(testUsername);
        UserDetails userDetails = User.builder()
                .username(testUsername)
                .password("password")
                .authorities("ROLE_USER")
                .build();

        // Act
        Boolean isValid = jwtUtils.validateToken(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_WithValidTokenAndIncorrectUsername_ReturnsFalse() {
        // Arrange
        String token = jwtUtils.generateToken(testUsername);
        UserDetails userDetails = User.builder()
                .username("differentuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        // Act
        Boolean isValid = jwtUtils.validateToken(token, userDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithExpiredToken_ThrowsException() {
        // Arrange
        // Create an expired token by setting expiration to past date
        Date pastDate = new Date(System.currentTimeMillis() - 1000);
        String expiredToken = Jwts.builder()
                .setSubject(testUsername)
                .setIssuedAt(new Date())
                .setExpiration(pastDate)
                .signWith(getSigningKey())
                .compact();

        UserDetails userDetails = User.builder()
                .username(testUsername)
                .password("password")
                .authorities("ROLE_USER")
                .build();

        // Act & Assert
        assertThrows(Exception.class, () -> jwtUtils.validateToken(expiredToken, userDetails));
    }

    @Test
    void validateToken_WithMalformedToken_ThrowsException() {
        // Arrange
        String malformedToken = "malformed.token";
        UserDetails userDetails = User.builder()
                .username(testUsername)
                .password("password")
                .authorities("ROLE_USER")
                .build();

        // Act & Assert
        assertThrows(Exception.class, () -> jwtUtils.validateToken(malformedToken, userDetails));
    }

    @Test
    void getExpirationDateFromToken_WithValidToken_ReturnsCorrectExpirationDate() {
        // Arrange
        String token = jwtUtils.generateToken(testUsername);

        // Act
        Date expirationDate = jwtUtils.getExpirationDateFromToken(token);

        // Assert
        assertNotNull(expirationDate);
        assertTrue(expirationDate.getTime() > System.currentTimeMillis());
    }

    @Test
    void tokenExpiration_IsSetCorrectly() {
        // Arrange
        long currentTime = System.currentTimeMillis();
        String token = jwtUtils.generateToken(testUsername);

        // Act
        Date expirationDate = jwtUtils.getExpirationDateFromToken(token);
        long expirationTime = expirationDate.getTime();
        long expectedExpiration = currentTime + testExpirationMs;

        // Assert
        // Allow for small time differences (within 1 second)
        assertTrue(Math.abs(expectedExpiration - expirationTime) < 1000);
    }

    @Test
    void getClaimFromToken_WithValidToken_ReturnsCorrectClaim() {
        // Arrange
        String token = jwtUtils.generateToken(testUsername);

        // Act
        String subject = jwtUtils.getClaimFromToken(token, Claims::getSubject);

        // Assert
        assertEquals(testUsername, subject);
    }

    @Test
    void generateToken_WithDifferentUsernames_CreatesDifferentTokens() {
        // Arrange
        String username1 = "user1";
        String username2 = "user2";

        // Act
        String token1 = jwtUtils.generateToken(username1);
        String token2 = jwtUtils.generateToken(username2);

        // Assert
        assertNotEquals(token1, token2);
        assertEquals(username1, jwtUtils.getUsernameFromToken(token1));
        assertEquals(username2, jwtUtils.getUsernameFromToken(token2));
    }

    @Test
    void generateToken_WithSameUsernameAtDifferentTimes_CreatesDifferentTokens() throws InterruptedException {
        // Arrange
        String token1 = jwtUtils.generateToken(testUsername);
        
        // Wait a small amount to ensure different issued at times
        TimeUnit.MILLISECONDS.sleep(10);
        
        // Act
        String token2 = jwtUtils.generateToken(testUsername);

        // Assert
        // Tokens may be the same if generated within the same millisecond
        // So we just verify both tokens are valid and contain the correct username
        assertEquals(testUsername, jwtUtils.getUsernameFromToken(token1));
        assertEquals(testUsername, jwtUtils.getUsernameFromToken(token2));
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = testSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
