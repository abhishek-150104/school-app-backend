package com.school.school_app.controller;

import com.school.school_app.dto.request.UpdateSchoolRequest;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.dto.response.SchoolResponse;
import com.school.school_app.service.SchoolContextService;
import com.school.school_app.service.SchoolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/school")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;
    private final SchoolContextService schoolContextService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<SchoolResponse>> getSchool() {
        return ResponseEntity.ok(ApiResponse.success(schoolService.getSchool(schoolContextService.getSchoolId())));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<SchoolResponse>> updateSchool(
            @Valid @RequestBody UpdateSchoolRequest request) {
        return ResponseEntity.ok(ApiResponse.success("School updated",
                schoolService.updateSchool(schoolContextService.getSchoolId(), request)));
    }
}
