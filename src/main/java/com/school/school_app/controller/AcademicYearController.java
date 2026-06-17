package com.school.school_app.controller;

import com.school.school_app.dto.request.CreateAcademicYearRequest;
import com.school.school_app.dto.response.AcademicYearResponse;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.service.AcademicYearService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schools/{schoolId}/academic-years")
@RequiredArgsConstructor
public class AcademicYearController {

    private final AcademicYearService academicYearService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<AcademicYearResponse>> create(
            @PathVariable Long schoolId,
            @Valid @RequestBody CreateAcademicYearRequest request) {
        AcademicYearResponse response = academicYearService.create(schoolId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Academic year created", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<AcademicYearResponse>>> getAll(@PathVariable Long schoolId) {
        return ResponseEntity.ok(ApiResponse.success(academicYearService.getAllBySchool(schoolId)));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'TEACHER', 'PARENT')")
    public ResponseEntity<ApiResponse<AcademicYearResponse>> getActive(@PathVariable Long schoolId) {
        return ResponseEntity.ok(ApiResponse.success(academicYearService.getActive(schoolId)));
    }

    @PatchMapping("/{yearId}/activate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<AcademicYearResponse>> activate(
            @PathVariable Long schoolId,
            @PathVariable Long yearId) {
        return ResponseEntity.ok(ApiResponse.success("Academic year activated", academicYearService.activate(schoolId, yearId)));
    }

    @DeleteMapping("/{yearId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long schoolId,
            @PathVariable Long yearId) {
        academicYearService.delete(schoolId, yearId);
        return ResponseEntity.ok(ApiResponse.success("Academic year deleted", null));
    }
}
