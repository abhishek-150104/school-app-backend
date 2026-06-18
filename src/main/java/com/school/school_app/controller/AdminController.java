package com.school.school_app.controller;

import com.school.school_app.dto.request.CreateAdminRequest;
import com.school.school_app.dto.response.AdminUserResponse;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<ApiResponse<AdminUserResponse>> createAdmin(
            @Valid @RequestBody CreateAdminRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Admin account created", adminService.createAdmin(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getAllAdmins() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllAdmins()));
    }

    @PutMapping("/{userId}/disable")
    public ResponseEntity<ApiResponse<Void>> disableAdmin(@PathVariable String userId) {
        adminService.disableAdmin(userId);
        return ResponseEntity.ok(ApiResponse.success("Admin account disabled", null));
    }
}
