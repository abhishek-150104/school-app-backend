package com.school.school_app.controller;

import com.school.school_app.dto.request.CreateSchoolRequest;
import com.school.school_app.dto.request.UpdateSchoolRequest;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.dto.response.SchoolResponse;
import com.school.school_app.service.SchoolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schools")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SchoolResponse>> createSchool(@Valid @RequestBody CreateSchoolRequest request) {
        SchoolResponse response = schoolService.createSchool(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("School created successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<SchoolResponse>>> getAllSchools() {
        return ResponseEntity.ok(ApiResponse.success(schoolService.getAllSchools()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'TEACHER', 'PARENT')")
    public ResponseEntity<ApiResponse<SchoolResponse>> getSchool(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(schoolService.getSchool(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<SchoolResponse>> updateSchool(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSchoolRequest request) {
        return ResponseEntity.ok(ApiResponse.success("School updated", schoolService.updateSchool(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSchool(@PathVariable Long id) {
        schoolService.deleteSchool(id);
        return ResponseEntity.ok(ApiResponse.success("School deactivated", null));
    }
}
