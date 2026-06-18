package com.school.school_app.controller;

import com.school.school_app.dto.request.CreateExamRequest;
import com.school.school_app.dto.request.EnterResultRequest;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.dto.response.ExamResponse;
import com.school.school_app.dto.response.ExamResultResponse;
import com.school.school_app.entity.User;
import com.school.school_app.service.ExamService;
import com.school.school_app.service.SchoolContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;
    private final SchoolContextService schoolContextService;

    @PostMapping("/api/exams")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<ExamResponse>> createExam(
            @RequestBody CreateExamRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Exam created",
                examService.createExam(schoolContextService.getSchoolId(),
                        schoolContextService.getSchoolName(), request,
                        currentUser.getId(), currentUser.getFullName())));
    }

    @GetMapping("/api/exams")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ExamResponse>>> getExams() {
        return ResponseEntity.ok(ApiResponse.success(
                examService.getExams(schoolContextService.getSchoolId())));
    }

    @GetMapping("/api/exams/{examId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ExamResponse>> getExam(@PathVariable String examId) {
        return ResponseEntity.ok(ApiResponse.success(
                examService.getExam(schoolContextService.getSchoolId(), examId)));
    }

    @PostMapping("/api/exams/results")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<ExamResultResponse>> enterResult(
            @RequestBody EnterResultRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Result entered",
                examService.enterResult(schoolContextService.getSchoolId(), request,
                        currentUser.getId(), currentUser.getFullName())));
    }

    @GetMapping("/api/exams/{examId}/results")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ExamResultResponse>>> getExamResults(
            @PathVariable String examId) {
        return ResponseEntity.ok(ApiResponse.success(
                examService.getExamResults(examId)));
    }

    @GetMapping("/api/students/{studentId}/results")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ExamResultResponse>>> getStudentResults(
            @PathVariable String studentId) {
        return ResponseEntity.ok(ApiResponse.success(
                examService.getStudentResults(schoolContextService.getSchoolId(), studentId)));
    }
}
