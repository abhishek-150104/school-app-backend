package com.school.school_app.controller;

import com.school.school_app.dto.request.CreateAcademicYearRequest;
import com.school.school_app.dto.response.AcademicYearResponse;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.service.AcademicYearService;
import com.school.school_app.service.SchoolContextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/academic-years")
@RequiredArgsConstructor
public class AcademicYearController {

    private final AcademicYearService academicYearService;
    private final SchoolContextService schoolContextService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<AcademicYearResponse>> create(
            @Valid @RequestBody CreateAcademicYearRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Academic year created",
                        academicYearService.create(schoolContextService.getSchoolId(), request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<AcademicYearResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(academicYearService.getAllBySchool(schoolContextService.getSchoolId())));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'TEACHER', 'PARENT')")
    public ResponseEntity<ApiResponse<AcademicYearResponse>> getActive() {
        return ResponseEntity.ok(ApiResponse.success(academicYearService.getActive(schoolContextService.getSchoolId())));
    }

    @PatchMapping("/{yearId}/activate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<AcademicYearResponse>> activate(@PathVariable String yearId) {
        return ResponseEntity.ok(ApiResponse.success("Academic year activated",
                academicYearService.activate(schoolContextService.getSchoolId(), yearId)));
    }

    @DeleteMapping("/{yearId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String yearId) {
        academicYearService.delete(schoolContextService.getSchoolId(), yearId);
        return ResponseEntity.ok(ApiResponse.success("Academic year deleted", null));
    }
}
