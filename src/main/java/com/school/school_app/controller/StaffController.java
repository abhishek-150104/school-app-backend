package com.school.school_app.controller;

import com.school.school_app.dto.request.CreateStaffRequest;
import com.school.school_app.dto.request.UpdateStaffRequest;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.dto.response.EnrollStaffResponse;
import com.school.school_app.dto.response.StaffResponse;
import com.school.school_app.dto.response.TeacherProfileResponse;
import com.school.school_app.entity.User;
import com.school.school_app.service.SchoolContextService;
import com.school.school_app.service.StaffService;
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
    private final SchoolContextService schoolContextService;

    @PostMapping("/api/staff")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<EnrollStaffResponse>> create(
            @Valid @RequestBody CreateStaffRequest request) {
        EnrollStaffResponse response = staffService.create(schoolContextService.getSchoolId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Staff member created", response));
    }

    @GetMapping("/api/staff")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<List<StaffResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(staffService.getAllBySchool(schoolContextService.getSchoolId())));
    }

    @GetMapping("/api/staff/search")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<List<StaffResponse>>> search(
            @RequestParam(required = false) String q) {
        return ResponseEntity.ok(ApiResponse.success(
                staffService.search(schoolContextService.getSchoolId(), q)));
    }

    @GetMapping("/api/staff/{staffId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<StaffResponse>> getById(@PathVariable String staffId) {
        return ResponseEntity.ok(ApiResponse.success(
                staffService.getById(schoolContextService.getSchoolId(), staffId)));
    }

    @PutMapping("/api/staff/{staffId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<StaffResponse>> update(
            @PathVariable String staffId,
            @Valid @RequestBody UpdateStaffRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                staffService.update(schoolContextService.getSchoolId(), staffId, request)));
    }

    @DeleteMapping("/api/staff/{staffId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable String staffId) {
        staffService.deactivate(schoolContextService.getSchoolId(), staffId);
        return ResponseEntity.ok(ApiResponse.success("Staff member deactivated", null));
    }

    @GetMapping("/api/teacher/me/profile")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<TeacherProfileResponse>> getMyProfile(
            @AuthenticationPrincipal User currentUser) {
        TeacherProfileResponse profile = staffService.getMyProfile(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }
}
