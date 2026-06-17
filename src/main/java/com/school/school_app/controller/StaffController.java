package com.school.school_app.controller;

import com.school.school_app.dto.request.CreateStaffRequest;
import com.school.school_app.dto.request.UpdateStaffRequest;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.dto.response.StaffResponse;
import com.school.school_app.dto.response.TeacherProfileResponse;
import com.school.school_app.service.StaffService;
import com.school.school_app.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    // ── School-scoped Staff endpoints ─────────────────────────────────────────

    @PostMapping("/api/schools/{schoolId}/staff")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<StaffResponse>> create(
            @PathVariable String schoolId,
            @Valid @RequestBody CreateStaffRequest request) {
        StaffResponse staff = staffService.create(schoolId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(staff, "Staff member created"));
    }

    @GetMapping("/api/schools/{schoolId}/staff")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<List<StaffResponse>>> getAll(
            @PathVariable String schoolId) {
        return ResponseEntity.ok(ApiResponse.success(staffService.getAllBySchool(schoolId)));
    }

    @GetMapping("/api/schools/{schoolId}/staff/search")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<List<StaffResponse>>> search(
            @PathVariable String schoolId,
            @RequestParam(required = false) String q) {
        return ResponseEntity.ok(ApiResponse.success(staffService.search(schoolId, q)));
    }

    @GetMapping("/api/schools/{schoolId}/staff/{staffId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<StaffResponse>> getById(
            @PathVariable String schoolId,
            @PathVariable String staffId) {
        return ResponseEntity.ok(ApiResponse.success(staffService.getById(schoolId, staffId)));
    }

    @PutMapping("/api/schools/{schoolId}/staff/{staffId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<StaffResponse>> update(
            @PathVariable String schoolId,
            @PathVariable String staffId,
            @Valid @RequestBody UpdateStaffRequest request) {
        return ResponseEntity.ok(ApiResponse.success(staffService.update(schoolId, staffId, request)));
    }

    @DeleteMapping("/api/schools/{schoolId}/staff/{staffId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(
            @PathVariable String schoolId,
            @PathVariable String staffId) {
        staffService.deactivate(schoolId, staffId);
        return ResponseEntity.ok(ApiResponse.success(null, "Staff member deactivated"));
    }

    // ── Teacher self-service ──────────────────────────────────────────────────

    @GetMapping("/api/teacher/me/profile")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<TeacherProfileResponse>> getMyProfile(
            @AuthenticationPrincipal User currentUser) {
        TeacherProfileResponse profile = staffService.getMyProfile(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }
}
