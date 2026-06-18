package com.school.school_app.controller;

import com.school.school_app.dto.request.CreateHomeworkRequest;
import com.school.school_app.dto.request.MarkSubmittedRequest;
import com.school.school_app.dto.request.UpdateHomeworkRequest;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.dto.response.HomeworkResponse;
import com.school.school_app.dto.response.HomeworkSubmissionResponse;
import com.school.school_app.entity.User;
import com.school.school_app.service.HomeworkService;
import com.school.school_app.service.SchoolContextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class HomeworkController {

    private final HomeworkService homeworkService;
    private final SchoolContextService schoolContextService;

    // POST /api/sections/{sectionId}/homework
    @PostMapping("/api/sections/{sectionId}/homework")
    @PreAuthorize("hasAnyRole('TEACHER', 'SCHOOL_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<HomeworkResponse>> create(
            @PathVariable String sectionId,
            @Valid @RequestBody CreateHomeworkRequest request,
            @AuthenticationPrincipal User currentUser) {

        HomeworkResponse response = homeworkService.create(
                schoolContextService.getSchoolId(), sectionId, request,
                currentUser.getId(), currentUser.getFullName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Homework created successfully", response));
    }

    // GET /api/sections/{sectionId}/homework?from=&to=
    @GetMapping("/api/sections/{sectionId}/homework")
    @PreAuthorize("hasAnyRole('TEACHER', 'SCHOOL_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<HomeworkResponse>>> getBySection(
            @PathVariable String sectionId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        if (from == null) from = LocalDate.now().minusDays(30);
        if (to == null) to = LocalDate.now().plusDays(30);

        return ResponseEntity.ok(ApiResponse.success(
                homeworkService.getBySection(schoolContextService.getSchoolId(), sectionId, from, to)));
    }

    // GET /api/homework/{homeworkId}
    @GetMapping("/api/homework/{homeworkId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'SCHOOL_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<HomeworkResponse>> getById(
            @PathVariable String homeworkId) {

        return ResponseEntity.ok(ApiResponse.success(
                homeworkService.getById(schoolContextService.getSchoolId(), homeworkId)));
    }

    // PUT /api/homework/{homeworkId}
    @PutMapping("/api/homework/{homeworkId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'SCHOOL_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<HomeworkResponse>> update(
            @PathVariable String homeworkId,
            @RequestBody UpdateHomeworkRequest request,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(ApiResponse.success("Homework updated successfully",
                homeworkService.update(schoolContextService.getSchoolId(), homeworkId,
                        request, currentUser.getId())));
    }

    // DELETE /api/homework/{homeworkId}
    @DeleteMapping("/api/homework/{homeworkId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'SCHOOL_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String homeworkId,
            @AuthenticationPrincipal User currentUser) {

        homeworkService.delete(schoolContextService.getSchoolId(), homeworkId,
                currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Homework deleted successfully"));
    }

    // POST /api/homework/{homeworkId}/submissions
    @PostMapping("/api/homework/{homeworkId}/submissions")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<HomeworkSubmissionResponse>> markSubmitted(
            @PathVariable String homeworkId,
            @Valid @RequestBody MarkSubmittedRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Submission recorded",
                        homeworkService.markSubmitted(schoolContextService.getSchoolId(),
                                homeworkId, request)));
    }

    // GET /api/homework/{homeworkId}/submissions
    @GetMapping("/api/homework/{homeworkId}/submissions")
    @PreAuthorize("hasAnyRole('TEACHER', 'SCHOOL_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<HomeworkSubmissionResponse>>> getSubmissions(
            @PathVariable String homeworkId) {

        return ResponseEntity.ok(ApiResponse.success(
                homeworkService.getSubmissions(schoolContextService.getSchoolId(), homeworkId)));
    }

    // GET /api/parents/me/students/{studentId}/homework?from=&to=
    @GetMapping("/api/parents/me/students/{studentId}/homework")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<List<HomeworkResponse>>> getMyChildHomework(
            @PathVariable String studentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @AuthenticationPrincipal User currentUser) {

        if (from == null) from = LocalDate.now().minusDays(30);
        if (to == null) to = LocalDate.now().plusDays(30);

        return ResponseEntity.ok(ApiResponse.success(
                homeworkService.getMyChildHomework(currentUser.getId(), studentId, from, to)));
    }
}
