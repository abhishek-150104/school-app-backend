package com.school.school_app.service;

import com.school.school_app.dto.request.*;
import com.school.school_app.dto.response.AuthResponse;
import com.school.school_app.entity.OtpToken;
import com.school.school_app.entity.RefreshToken;
import com.school.school_app.entity.Role;
import com.school.school_app.entity.User;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.OtpTokenRepository;
import com.school.school_app.repository.RefreshTokenRepository;
import com.school.school_app.repository.UserRepository;
import com.school.school_app.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock OtpTokenRepository otpTokenRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock JwtService jwtService;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AuthenticationManager authenticationManager;
    @Mock EmailService emailService;

    @InjectMocks AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-1")
                .fullName("Test User")
                .email("test@example.com")
                .phone("9876543210")
                .password("encoded-password")
                .role(Role.PARENT)
                .enabled(true)
                .build();
    }

    // ── register ──────────────────────────────────────────────────────────────

    @Test
    void register_withParentRole_shouldReturnAuthResponse() {
        RegisterRequest req = new RegisterRequest();
        req.setFullName("Test User");
        req.setEmail("test@example.com");
        req.setPassword("password123");
        req.setRole(Role.PARENT);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(testUser);
        when(jwtService.generateToken(any())).thenReturn("access-token");
        when(refreshTokenRepository.findByUserIdAndRevokedFalse(anyString())).thenReturn(List.of());
        when(refreshTokenRepository.save(any())).thenReturn(new RefreshToken());

        AuthResponse result = authService.register(req);

        assertThat(result.getAccessToken()).isEqualTo("access-token");
        assertThat(result.getUserId()).isEqualTo("user-1");
    }

    @Test
    void register_withNonParentRole_shouldThrowForbidden() {
        RegisterRequest req = new RegisterRequest();
        req.setRole(Role.SUPER_ADMIN);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void register_withDuplicateEmail_shouldThrowConflict() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("taken@example.com");
        req.setRole(Role.PARENT);

        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void register_withDuplicatePhone_shouldThrowConflict() {
        RegisterRequest req = new RegisterRequest();
        req.setPhone("9876543210");
        req.setRole(Role.PARENT);

        when(userRepository.existsByPhone("9876543210")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    // ── login ─────────────────────────────────────────────────────────────────

    @Test
    void login_withValidCredentials_shouldReturnAuthResponse() {
        LoginRequest req = new LoginRequest();
        req.setUsername("test@example.com");
        req.setPassword("password123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any())).thenReturn("access-token");
        when(refreshTokenRepository.findByUserIdAndRevokedFalse(anyString())).thenReturn(List.of());
        when(refreshTokenRepository.save(any())).thenReturn(new RefreshToken());

        AuthResponse result = authService.login(req);

        assertThat(result.getAccessToken()).isEqualTo("access-token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_withBadCredentials_shouldThrow() {
        LoginRequest req = new LoginRequest();
        req.setUsername("test@example.com");
        req.setPassword("wrong");

        doThrow(new BadCredentialsException("bad"))
                .when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_userNotFoundAfterAuth_shouldThrowNotFound() {
        LoginRequest req = new LoginRequest();
        req.setUsername("ghost@example.com");
        req.setPassword("pass");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByPhone(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ── OTP ───────────────────────────────────────────────────────────────────

    @Test
    void sendOtp_withRegisteredPhone_shouldSaveToken() {
        OtpRequest req = new OtpRequest();
        req.setPhone("9876543210");

        when(userRepository.existsByPhone("9876543210")).thenReturn(true);

        authService.sendOtp(req);

        verify(otpTokenRepository).deleteAllByPhone("9876543210");
        verify(otpTokenRepository).save(argThat(t -> t.getPhone().equals("9876543210") && !t.isUsed()));
    }

    @Test
    void sendOtp_withUnregisteredPhone_shouldThrowNotFound() {
        OtpRequest req = new OtpRequest();
        req.setPhone("0000000000");

        when(userRepository.existsByPhone("0000000000")).thenReturn(false);

        assertThatThrownBy(() -> authService.sendOtp(req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void verifyOtp_withValidOtp_shouldReturnAuthResponse() {
        OtpVerifyRequest req = new OtpVerifyRequest();
        req.setPhone("9876543210");
        req.setOtp("123456");

        OtpToken token = OtpToken.builder()
                .phone("9876543210")
                .otp("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();

        when(otpTokenRepository.findTopByPhoneAndUsedFalseOrderByCreatedAtDesc("9876543210"))
                .thenReturn(Optional.of(token));
        when(userRepository.findByPhone("9876543210")).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any())).thenReturn("access-token");
        when(refreshTokenRepository.findByUserIdAndRevokedFalse(anyString())).thenReturn(List.of());
        when(refreshTokenRepository.save(any())).thenReturn(new RefreshToken());

        AuthResponse result = authService.verifyOtp(req);

        assertThat(result.getAccessToken()).isEqualTo("access-token");
        assertThat(token.isUsed()).isTrue();
    }

    @Test
    void verifyOtp_withExpiredOtp_shouldThrowBadRequest() {
        OtpVerifyRequest req = new OtpVerifyRequest();
        req.setPhone("9876543210");
        req.setOtp("123456");

        OtpToken token = OtpToken.builder()
                .otp("123456")
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .used(false)
                .build();

        when(otpTokenRepository.findTopByPhoneAndUsedFalseOrderByCreatedAtDesc(anyString()))
                .thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.verifyOtp(req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void verifyOtp_withWrongOtp_shouldThrowBadRequest() {
        OtpVerifyRequest req = new OtpVerifyRequest();
        req.setPhone("9876543210");
        req.setOtp("000000");

        OtpToken token = OtpToken.builder()
                .otp("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();

        when(otpTokenRepository.findTopByPhoneAndUsedFalseOrderByCreatedAtDesc(anyString()))
                .thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.verifyOtp(req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // ── refresh token ─────────────────────────────────────────────────────────

    @Test
    void refreshToken_withValidToken_shouldReturnNewAccessToken() {
        RefreshTokenRequest req = new RefreshTokenRequest();
        req.setRefreshToken("valid-refresh-token");

        RefreshToken stored = RefreshToken.builder()
                .token("valid-refresh-token")
                .userId("user-1")
                .expiresAt(LocalDateTime.now().plusDays(10))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByToken("valid-refresh-token")).thenReturn(Optional.of(stored));
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any())).thenReturn("new-access-token");

        AuthResponse result = authService.refreshToken(req);

        assertThat(result.getAccessToken()).isEqualTo("new-access-token");
        assertThat(result.getRefreshToken()).isEqualTo("valid-refresh-token");
    }

    @Test
    void refreshToken_withRevokedToken_shouldThrowUnauthorized() {
        RefreshTokenRequest req = new RefreshTokenRequest();
        req.setRefreshToken("revoked-token");

        RefreshToken stored = RefreshToken.builder()
                .token("revoked-token")
                .expiresAt(LocalDateTime.now().plusDays(10))
                .revoked(true)
                .build();

        when(refreshTokenRepository.findByToken("revoked-token")).thenReturn(Optional.of(stored));

        assertThatThrownBy(() -> authService.refreshToken(req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void refreshToken_withExpiredToken_shouldThrowUnauthorized() {
        RefreshTokenRequest req = new RefreshTokenRequest();
        req.setRefreshToken("expired-token");

        RefreshToken stored = RefreshToken.builder()
                .token("expired-token")
                .expiresAt(LocalDateTime.now().minusDays(1))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(stored));

        assertThatThrownBy(() -> authService.refreshToken(req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // ── logout ────────────────────────────────────────────────────────────────

    @Test
    void logout_shouldRevokeAllActiveTokens() {
        RefreshToken t1 = RefreshToken.builder().token("t1").revoked(false).build();
        RefreshToken t2 = RefreshToken.builder().token("t2").revoked(false).build();

        when(refreshTokenRepository.findByUserIdAndRevokedFalse("user-1")).thenReturn(List.of(t1, t2));

        authService.logout(testUser);

        assertThat(t1.isRevoked()).isTrue();
        assertThat(t2.isRevoked()).isTrue();
        verify(refreshTokenRepository).saveAll(List.of(t1, t2));
    }

    @Test
    void logout_withNoActiveTokens_shouldNotThrow() {
        when(refreshTokenRepository.findByUserIdAndRevokedFalse("user-1")).thenReturn(List.of());

        assertThatCode(() -> authService.logout(testUser)).doesNotThrowAnyException();
    }
}
