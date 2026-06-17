package com.school.school_app.service;

import com.school.school_app.dto.request.ChangePasswordRequest;
import com.school.school_app.dto.request.UpdateProfileRequest;
import com.school.school_app.dto.response.UserProfileResponse;
import com.school.school_app.entity.Role;
import com.school.school_app.entity.User;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-1")
                .fullName("Test User")
                .email("test@example.com")
                .phone("9876543210")
                .password("encoded-current")
                .role(Role.PARENT)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getProfile_shouldReturnMappedResponse() {
        UserProfileResponse result = userService.getProfile(testUser);

        assertThat(result.getId()).isEqualTo("user-1");
        assertThat(result.getFullName()).isEqualTo("Test User");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getRole()).isEqualTo(Role.PARENT);
    }

    @Test
    void updateProfile_withNewName_shouldUpdateAndReturn() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setFullName("Updated Name");

        when(userRepository.save(any())).thenReturn(testUser);

        UserProfileResponse result = userService.updateProfile(testUser, req);

        assertThat(testUser.getFullName()).isEqualTo("Updated Name");
        verify(userRepository).save(testUser);
    }

    @Test
    void updateProfile_withNewPhone_shouldUpdate() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setFullName("Test User");
        req.setPhone("1111111111");

        when(userRepository.existsByPhone("1111111111")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(testUser);

        userService.updateProfile(testUser, req);

        assertThat(testUser.getPhone()).isEqualTo("1111111111");
    }

    @Test
    void updateProfile_withDuplicatePhone_shouldThrowConflict() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setFullName("Test");
        req.setPhone("1111111111");

        when(userRepository.existsByPhone("1111111111")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateProfile(testUser, req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateProfile_withSamePhone_shouldNotCheckDuplicate() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setFullName("Test User");
        req.setPhone("9876543210"); // same as current

        when(userRepository.save(any())).thenReturn(testUser);

        assertThatCode(() -> userService.updateProfile(testUser, req)).doesNotThrowAnyException();
        verify(userRepository, never()).existsByPhone(anyString());
    }

    @Test
    void changePassword_withCorrectCurrentPassword_shouldUpdate() {
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setCurrentPassword("current");
        req.setNewPassword("newpass123");

        when(passwordEncoder.matches("current", "encoded-current")).thenReturn(true);
        when(passwordEncoder.encode("newpass123")).thenReturn("encoded-new");
        when(userRepository.save(any())).thenReturn(testUser);

        userService.changePassword(testUser, req);

        assertThat(testUser.getPassword()).isEqualTo("encoded-new");
        verify(userRepository).save(testUser);
    }

    @Test
    void changePassword_withWrongCurrentPassword_shouldThrowBadRequest() {
        ChangePasswordRequest req = new ChangePasswordRequest();
        req.setCurrentPassword("wrong");
        req.setNewPassword("newpass123");

        when(passwordEncoder.matches("wrong", "encoded-current")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(testUser, req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(userRepository, never()).save(any());
    }
}
