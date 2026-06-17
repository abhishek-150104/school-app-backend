package com.school.school_app.controller;

import com.school.school_app.dto.request.BulkMarkAttendanceRequest;
import com.school.school_app.dto.request.UpdateAttendanceRequest;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.dto.response.AttendanceResponse;
import com.school.school_app.dto.response.AttendanceSummaryResponse;
import com.school.school_app.entity.User;
import com.school.school_app.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/api/schools/{schoolId}/sections/{sectionId}/attendance")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> markBulk(
            @PathVariable String schoolId,
            @PathVariable String sectionId,
            @Valid @RequestBody BulkMarkAttendanceRequest request,
            @AuthenticationPrincipal User currentUser) {

        List<AttendanceResponse> result = attendanceService.markBulk(
                schoolId, sectionId, request,
                currentUser.getId(), currentUser.getFullName());

        return ResponseEntity.ok(ApiResponse.success("Attendance marked successfully", result));
    }

    @GetMapping("/api/schools/{schoolId}/sections/{sectionId}/attendance")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getBySection(
            @PathVariable String schoolId,
            @PathVariable String sectionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(
                ApiResponse.success(attendanceService.getBySection(schoolId, sectionId, date)));
    }

    @GetMapping("/api/schools/{schoolId}/sections/{sectionId}/attendance/summary")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<AttendanceSummaryResponse>> getSummary(
            @PathVariable String schoolId,
            @PathVariable String sectionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ResponseEntity.ok(
                ApiResponse.success(attendanceService.getSummary(schoolId, sectionId, from, to)));
    }

    @GetMapping("/api/schools/{schoolId}/students/{studentId}/attendance")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getByStudent(
            @PathVariable String schoolId,
            @PathVariable String studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ResponseEntity.ok(
                ApiResponse.success(attendanceService.getByStudent(schoolId, studentId, from, to)));
    }

    @PutMapping("/api/schools/{schoolId}/attendance/{attendanceId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> update(
            @PathVariable String schoolId,
            @PathVariable String attendanceId,
            @RequestBody UpdateAttendanceRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success("Attendance updated",
                        attendanceService.update(schoolId, attendanceId, request)));
    }

    @GetMapping("/api/parents/me/students/{studentId}/attendance")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getMyChildAttendance(
            @PathVariable String studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(
                ApiResponse.success(attendanceService.getMyChildAttendance(
                        currentUser.getId(), studentId, from, to)));
    }
}
