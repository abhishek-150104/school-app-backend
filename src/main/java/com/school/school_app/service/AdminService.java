package com.school.school_app.service;

import com.school.school_app.dto.request.CreateAdminRequest;
import com.school.school_app.dto.response.AdminUserResponse;
import com.school.school_app.entity.Role;
import com.school.school_app.entity.User;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AdminUserResponse createAdmin(CreateAdminRequest request) {
        if (request.getEmail() == null && request.getPhone() == null) {
            throw new AppException("Email or phone is required for the admin account", HttpStatus.BAD_REQUEST);
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email already in use", HttpStatus.CONFLICT);
        }
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new AppException("Phone already in use", HttpStatus.CONFLICT);
        }

        User admin = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.SCHOOL_ADMIN)
                .enabled(true)
                .passwordChanged(true)
                .build();

        return AdminUserResponse.from(userRepository.save(admin));
    }

    public List<AdminUserResponse> getAllAdmins() {
        return userRepository.findByRole(Role.SCHOOL_ADMIN).stream()
                .map(AdminUserResponse::from)
                .toList();
    }

    @Transactional
    public void disableAdmin(String userId) {
        User admin = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        if (admin.getRole() != Role.SCHOOL_ADMIN) {
            throw new AppException("User is not a SCHOOL_ADMIN", HttpStatus.BAD_REQUEST);
        }
        admin.setEnabled(false);
        userRepository.save(admin);
    }
}
