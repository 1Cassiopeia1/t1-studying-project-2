package ru.t1.transaction.acceptation.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import ru.t1.transaction.acceptation.dto.enums.Role;
import ru.t1.transaction.acceptation.model.User;
import ru.t1.transaction.acceptation.service.impl.security.JwtService;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test for JwtService")
public class JwtServiceTest {

    @Mock
    private UserDetails userDetails;
    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    public void setUp() {
        jwtService.setJwtSigningKey("""
                eyJhbGciOiJIUzI1NiJ9eyJSb2xlIjoiQWRtaW4iLCJJc3N1ZX\
                IiOiJJc3N1ZXIiLCJVc2VybmFtZSI6IkphdmFJblVzZSIsImV4cCI6MTczMjY3MzcyMCwiaWF0\
                IjoxNzMyNjczNzIwfQIFEirwVtv16jKuTqzXI6gT5vD567DTPG4gusyTtqHGMTk""");
        userDetails = new User()
                .setId(1L)
                .setUsername("testUser")
                .setPassword("password123")
                .setRole(Role.ROLE_USER);
    }

    @Test
    public void testGenerateToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        // JWT токены начинаются с "ey"
        assertTrue(token.startsWith("ey"));
    }

    @Test
    public void testExtractUserName() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUserName(token);

        assertEquals("testUser", username);
    }

    @Test
    public void testIsTokenValid_validToken() {
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    public void testIsTokenValid_invalidToken() {
        String token = jwtService.generateToken(userDetails);
        // Создаем недействительный токен
        String invalidToken = token + "invalid";
        SignatureException exception =
                assertThrows(SignatureException.class, () -> jwtService.isTokenValid(invalidToken, userDetails));

        assertNotNull(exception);
        assertThat(exception.getMessage()).contains("JWT signature does not match locally computed signature");
    }

    @Test
    public void testIsTokenExpired_expiredToken() {
        // Создаем токен с истекшим сроком действия
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 1L);
        claims.put("role", "USER");

        String expiredToken = Jwts.builder()
                .claims(claims)
                .subject("testUser")
                // Устанавливаем время выдачи в прошлом
                .issuedAt(new Date(System.currentTimeMillis() - 100000 * 60 * 24))
                // Устанавливаем время истечения в прошлом
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(getSigningKey())
                .compact();

        ExpiredJwtException exception =
                assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(expiredToken, userDetails));

        assertNotNull(exception);
        assertThat(exception.getMessage()).containsSubsequence("JWT expired", "milliseconds ago at ", "Current time: ");
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtService.getJwtSigningKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
