package com.school.school_app.service;

import com.school.school_app.dto.request.*;
import com.school.school_app.dto.response.AuthResponse;
import com.school.school_app.entity.OtpToken;
import com.school.school_app.entity.RefreshToken;
import com.school.school_app.entity.User;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.OtpTokenRepository;
import com.school.school_app.repository.RefreshTokenRepository;
import com.school.school_app.repository.UserRepository;
import com.school.school_app.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (request.getRole() != com.school.school_app.entity.Role.PARENT) {
            throw new AppException("Self-registration is only allowed for PARENT role", HttpStatus.FORBIDDEN);
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email already registered", HttpStatus.CONFLICT);
        }
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new AppException("Phone already registered", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();

        user = userRepository.save(user);
        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getUsername())
                .or(() -> userRepository.findByPhone(request.getUsername()))
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        return buildAuthResponse(user);
    }

    @Transactional
    public void sendOtp(OtpRequest request) {
        if (!userRepository.existsByPhone(request.getPhone())) {
            throw new AppException("No account found with this phone number", HttpStatus.NOT_FOUND);
        }

        otpTokenRepository.deleteAllByPhone(request.getPhone());

        String otp = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
        OtpToken token = OtpToken.builder()
                .phone(request.getPhone())
                .otp(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();

        otpTokenRepository.save(token);
        // TODO: integrate SMS provider (Twilio / AWS SNS) to send OTP
    }

    @Transactional
    public AuthResponse verifyOtp(OtpVerifyRequest request) {
        OtpToken token = otpTokenRepository
                .findTopByPhoneAndUsedFalseOrderByCreatedAtDesc(request.getPhone())
                .orElseThrow(() -> new AppException("OTP not found or already used", HttpStatus.BAD_REQUEST));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException("OTP expired", HttpStatus.BAD_REQUEST);
        }
        if (!token.getOtp().equals(request.getOtp())) {
            throw new AppException("Invalid OTP", HttpStatus.BAD_REQUEST);
        }

        token.setUsed(true);
        otpTokenRepository.save(token);

        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        return buildAuthResponse(user);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        // Always return success to prevent user enumeration
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            otpTokenRepository.deleteAllByPhone(user.getEmail());
            String resetToken = UUID.randomUUID().toString();
            OtpToken token = OtpToken.builder()
                    .phone(user.getEmail())
                    .otp(resetToken)
                    .expiresAt(LocalDateTime.now().plusHours(1))
                    .used(false)
                    .build();
            otpTokenRepository.save(token);
            emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        OtpToken token = otpTokenRepository
                .findTopByPhoneAndUsedFalseOrderByCreatedAtDesc(request.getToken())
                .orElseThrow(() -> new AppException("Invalid or expired reset token", HttpStatus.BAD_REQUEST));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException("Reset token expired", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByEmail(token.getPhone())
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        token.setUsed(true);
        otpTokenRepository.save(token);
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new AppException("Invalid refresh token", HttpStatus.UNAUTHORIZED));

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException("Refresh token expired or revoked", HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        String newAccessToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(stored.getToken())
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    @Transactional
    public void logout(User user) {
        revokeAllTokens(user.getId());
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user);

        revokeAllTokens(user.getId());
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .userId(user.getId())
                .expiresAt(LocalDateTime.now().plusDays(30))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .role(user.getRole())
                .firstLogin(!user.isPasswordChanged())
                .build();
    }

    @Transactional
    public void setupAccount(com.school.school_app.dto.request.SetupAccountRequest request, User currentUser) {
        if (request.getEmail() != null && !request.getEmail().equals(currentUser.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new AppException("Email already in use", HttpStatus.CONFLICT);
            }
            currentUser.setEmail(request.getEmail());
        }
        if (request.getPhone() != null && !request.getPhone().equals(currentUser.getPhone())) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new AppException("Phone already in use", HttpStatus.CONFLICT);
            }
            currentUser.setPhone(request.getPhone());
        }
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        currentUser.setPasswordChanged(true);
        userRepository.save(currentUser);
    }

    private void revokeAllTokens(String userId) {
        List<RefreshToken> tokens = refreshTokenRepository.findByUserIdAndRevokedFalse(userId);
        tokens.forEach(t -> t.setRevoked(true));
        refreshTokenRepository.saveAll(tokens);
    }
}
