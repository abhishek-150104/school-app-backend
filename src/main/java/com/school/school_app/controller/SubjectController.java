package com.school.school_app.controller;

import com.school.school_app.dto.request.CreateSubjectRequest;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.dto.response.SubjectResponse;
import com.school.school_app.entity.User;
import com.school.school_app.service.SchoolContextService;
import com.school.school_app.service.SubjectService;
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
public class SubjectController {

    private final SubjectService subjectService;
    private final SchoolContextService schoolContextService;

    @PostMapping("/api/classrooms/{classId}/subjects")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<SubjectResponse>> create(
            @PathVariable String classId,
            @Valid @RequestBody CreateSubjectRequest request,
            @AuthenticationPrincipal User currentUser) {

        SubjectResponse response = subjectService.create(
                schoolContextService.getSchoolId(), classId, request,
                currentUser.getId(), currentUser.getFullName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subject created successfully", response));
    }

    @GetMapping("/api/classrooms/{classId}/subjects")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> getAll(@PathVariable String classId) {
        return ResponseEntity.ok(ApiResponse.success(
                subjectService.getByClassRoom(schoolContextService.getSchoolId(), classId)));
    }

    @DeleteMapping("/api/classrooms/{classId}/subjects/{subjectId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String classId,
            @PathVariable String subjectId) {

        subjectService.delete(schoolContextService.getSchoolId(), classId, subjectId);
        return ResponseEntity.ok(ApiResponse.success("Subject deleted successfully"));
    }
}
