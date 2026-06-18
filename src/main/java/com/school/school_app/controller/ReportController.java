package com.school.school_app.controller;

import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.dto.response.ReportResponse;
import com.school.school_app.service.ReportService;
import com.school.school_app.service.SchoolContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final SchoolContextService schoolContextService;

    @GetMapping("/api/reports/dashboard")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<ReportResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(
                reportService.getDashboardReport(schoolContextService.getSchoolId())));
    }

    @GetMapping("/api/reports/fees")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<ReportResponse>> getFeeReport() {
        return ResponseEntity.ok(ApiResponse.success(
                reportService.getFeeReport(schoolContextService.getSchoolId())));
    }

    @GetMapping("/api/reports/attendance")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<ReportResponse>> getAttendanceReport() {
        return ResponseEntity.ok(ApiResponse.success(
                reportService.getAttendanceReport(schoolContextService.getSchoolId())));
    }

    @GetMapping("/api/reports/exams")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<ReportResponse>> getExamReport() {
        return ResponseEntity.ok(ApiResponse.success(
                reportService.getExamReport(schoolContextService.getSchoolId())));
    }
}
