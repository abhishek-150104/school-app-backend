package com.school.school_app.controller;

import com.school.school_app.dto.request.AssignTeacherRequest;
import com.school.school_app.dto.request.CreateSectionRequest;
import com.school.school_app.dto.request.UpdateSectionRequest;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.dto.response.SectionResponse;
import com.school.school_app.service.SectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes/{classId}/sections")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<SectionResponse>> create(
            @PathVariable Long classId,
            @Valid @RequestBody CreateSectionRequest request) {
        SectionResponse response = sectionService.create(classId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Section created", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'TEACHER', 'PARENT')")
    public ResponseEntity<ApiResponse<List<SectionResponse>>> getAll(@PathVariable Long classId) {
        return ResponseEntity.ok(ApiResponse.success(sectionService.getAllByClass(classId)));
    }

    @GetMapping("/{sectionId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'TEACHER', 'PARENT')")
    public ResponseEntity<ApiResponse<SectionResponse>> getById(
            @PathVariable Long classId,
            @PathVariable Long sectionId) {
        return ResponseEntity.ok(ApiResponse.success(sectionService.getById(classId, sectionId)));
    }

    @PutMapping("/{sectionId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<SectionResponse>> update(
            @PathVariable Long classId,
            @PathVariable Long sectionId,
            @Valid @RequestBody UpdateSectionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Section updated", sectionService.update(classId, sectionId, request)));
    }

    @PatchMapping("/{sectionId}/assign-teacher")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<SectionResponse>> assignTeacher(
            @PathVariable Long classId,
            @PathVariable Long sectionId,
            @Valid @RequestBody AssignTeacherRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Teacher assigned", sectionService.assignTeacher(classId, sectionId, request)));
    }

    @DeleteMapping("/{sectionId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long classId,
            @PathVariable Long sectionId) {
        sectionService.delete(classId, sectionId);
        return ResponseEntity.ok(ApiResponse.success("Section deleted", null));
    }
}
