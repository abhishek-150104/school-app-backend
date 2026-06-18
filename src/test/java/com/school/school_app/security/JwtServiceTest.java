package com.school.school_app.security;

import com.school.school_app.entity.Role;
import com.school.school_app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    // 48-byte key (> 256 bits required for HMAC-SHA256)
    private static final String TEST_SECRET =
            Base64.getEncoder().encodeToString("testSecretKeyForUnitTestingPurposesOnly12345".getBytes());

    private JwtService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 86_400_000L); // 1 day

        testUser = User.builder()
                .id("user-1")
                .email("test@example.com")
                .role(Role.PARENT)
                .enabled(true)
                .build();
    }

    @Test
    void generateToken_shouldReturnNonNullToken() {
        String token = jwtService.generateToken(testUser);

        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void extractUsername_shouldReturnUserEmail() {
        String token = jwtService.generateToken(testUser);

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("test@example.com");
    }

    @Test
    void isTokenValid_withValidToken_shouldReturnTrue() {
        String token = jwtService.generateToken(testUser);

        assertThat(jwtService.isTokenValid(token, testUser)).isTrue();
    }

    @Test
    void isTokenValid_withDifferentUser_shouldReturnFalse() {
        String token = jwtService.generateToken(testUser);

        User otherUser = User.builder()
                .id("other")
                .email("other@example.com")
                .role(Role.TEACHER)
                .enabled(true)
                .build();

        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    void isTokenValid_withExpiredToken_shouldReturnFalse() {
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", -1000L); // already expired

        String token = jwtService.generateToken(testUser);

        // expired tokens throw on parse — isTokenValid should return false
        assertThatCode(() -> {
            boolean valid = jwtService.isTokenValid(token, testUser);
            assertThat(valid).isFalse();
        }).doesNotThrowAnyException();
    }

    @Test
    void generateToken_withPhoneOnlyUser_shouldUsePhoneAsSubject() {
        User phoneUser = User.builder()
                .id("user-2")
                .phone("9876543210")
                .role(Role.PARENT)
                .enabled(true)
                .build();

        String token = jwtService.generateToken(phoneUser);
        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("9876543210");
    }
}
