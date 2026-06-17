package com.school.school_app.controller;

import com.school.school_app.dto.request.CreateStudentRequest;
import com.school.school_app.dto.request.LinkParentRequest;
import com.school.school_app.dto.request.TransferStudentRequest;
import com.school.school_app.dto.request.UpdateStudentRequest;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.dto.response.EnrollStudentResponse;
import com.school.school_app.dto.response.StudentResponse;
import com.school.school_app.entity.User;
import com.school.school_app.service.SchoolContextService;
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
    private final SchoolContextService schoolContextService;

    @PostMapping("/api/students")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<EnrollStudentResponse>> enroll(
            @Valid @RequestBody CreateStudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student enrolled successfully",
                        studentService.enroll(schoolContextService.getSchoolId(), request)));
    }

    @GetMapping("/api/students")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAll(
            @RequestParam(required = false) String classRoomId,
            @RequestParam(required = false) String sectionId) {
        return ResponseEntity.ok(ApiResponse.success(
                studentService.getAllBySchool(schoolContextService.getSchoolId(), classRoomId, sectionId)));
    }

    @GetMapping("/api/students/search")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> search(
            @RequestParam(required = false) String query) {
        return ResponseEntity.ok(ApiResponse.success(
                studentService.search(schoolContextService.getSchoolId(), query)));
    }

    @GetMapping("/api/students/{studentId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<StudentResponse>> getById(@PathVariable String studentId) {
        return ResponseEntity.ok(ApiResponse.success(
                studentService.getById(schoolContextService.getSchoolId(), studentId)));
    }

    @PutMapping("/api/students/{studentId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponse>> update(
            @PathVariable String studentId,
            @Valid @RequestBody UpdateStudentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                studentService.update(schoolContextService.getSchoolId(), studentId, request)));
    }

    @DeleteMapping("/api/students/{studentId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable String studentId) {
        studentService.deactivate(schoolContextService.getSchoolId(), studentId);
        return ResponseEntity.ok(ApiResponse.success("Student deactivated", null));
    }

    @PostMapping("/api/students/{studentId}/link-parent")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponse>> linkParent(
            @PathVariable String studentId,
            @Valid @RequestBody LinkParentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Parent linked successfully",
                studentService.linkParent(schoolContextService.getSchoolId(), studentId, request)));
    }

    @PostMapping("/api/students/{studentId}/transfer")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponse>> transfer(
            @PathVariable String studentId,
            @Valid @RequestBody TransferStudentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Student transferred successfully",
                studentService.transfer(schoolContextService.getSchoolId(), studentId, request)));
    }

    @GetMapping("/api/parents/me/students")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getMyChildren(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getMyChildren(currentUser.getId())));
    }
}
