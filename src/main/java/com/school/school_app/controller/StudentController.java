package com.school.school_app.controller;

import com.school.school_app.dto.request.CreateStudentRequest;
import com.school.school_app.dto.request.LinkParentRequest;
import com.school.school_app.dto.request.TransferStudentRequest;
import com.school.school_app.dto.request.UpdateStudentRequest;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.dto.response.StudentResponse;
import com.school.school_app.entity.User;
import com.school.school_app.service.StudentService;
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
public class StudentController {

    private final StudentService studentService;

    // ── Enrollment ────────────────────────────────────────────────────────────

    @PostMapping("/api/schools/{schoolId}/students")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponse>> enroll(
            @PathVariable String schoolId,
            @Valid @RequestBody CreateStudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(studentService.enroll(schoolId, request), "Student enrolled successfully"));
    }

    // ── List / Search ─────────────────────────────────────────────────────────

    @GetMapping("/api/schools/{schoolId}/students")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAll(
            @PathVariable String schoolId,
            @RequestParam(required = false) String classRoomId,
            @RequestParam(required = false) String sectionId) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getAllBySchool(schoolId, classRoomId, sectionId)));
    }

    @GetMapping("/api/schools/{schoolId}/students/search")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> search(
            @PathVariable String schoolId,
            @RequestParam(required = false) String query) {
        return ResponseEntity.ok(ApiResponse.success(studentService.search(schoolId, query)));
    }

    // ── Single student ────────────────────────────────────────────────────────

    @GetMapping("/api/schools/{schoolId}/students/{studentId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<StudentResponse>> getById(
            @PathVariable String schoolId,
            @PathVariable String studentId) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getById(schoolId, studentId)));
    }

    @PutMapping("/api/schools/{schoolId}/students/{studentId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponse>> update(
            @PathVariable String schoolId,
            @PathVariable String studentId,
            @Valid @RequestBody UpdateStudentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(studentService.update(schoolId, studentId, request)));
    }

    @DeleteMapping("/api/schools/{schoolId}/students/{studentId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(
            @PathVariable String schoolId,
            @PathVariable String studentId) {
        studentService.deactivate(schoolId, studentId);
        return ResponseEntity.ok(ApiResponse.success(null, "Student deactivated"));
    }

    // ── Parent link / Transfer ─────────────────────────────────────────────────

    @PostMapping("/api/schools/{schoolId}/students/{studentId}/link-parent")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponse>> linkParent(
            @PathVariable String schoolId,
            @PathVariable String studentId,
            @Valid @RequestBody LinkParentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(studentService.linkParent(schoolId, studentId, request), "Parent linked successfully"));
    }

    @PostMapping("/api/schools/{schoolId}/students/{studentId}/transfer")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponse>> transfer(
            @PathVariable String schoolId,
            @PathVariable String studentId,
            @Valid @RequestBody TransferStudentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(studentService.transfer(schoolId, studentId, request), "Student transferred successfully"));
    }

    // ── Parent's own children ─────────────────────────────────────────────────

    @GetMapping("/api/parents/me/students")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getMyChildren(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getMyChildren(currentUser.getId())));
    }
}
