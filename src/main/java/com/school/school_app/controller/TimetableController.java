package com.school.school_app.controller;

import com.school.school_app.dto.request.CreateTimetableEntryRequest;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.dto.response.TimetableEntryResponse;
import com.school.school_app.entity.User;
import com.school.school_app.service.SchoolContextService;
import com.school.school_app.service.TimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;
    private final SchoolContextService schoolContextService;

    @PostMapping("/api/timetable")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<TimetableEntryResponse>> addEntry(
            @RequestBody CreateTimetableEntryRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Entry added",
                timetableService.addEntry(schoolContextService.getSchoolId(),
                        schoolContextService.getSchoolName(), request,
                        currentUser.getId(), currentUser.getFullName())));
    }

    @GetMapping("/api/sections/{sectionId}/timetable")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<TimetableEntryResponse>>> getSectionTimetable(
            @PathVariable String sectionId) {
        return ResponseEntity.ok(ApiResponse.success(
                timetableService.getSectionTimetable(schoolContextService.getSchoolId(), sectionId)));
    }

    @GetMapping("/api/teacher/me/timetable")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<TimetableEntryResponse>>> getMyTimetable(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                timetableService.getTeacherTimetableByUser(schoolContextService.getSchoolId(), currentUser.getId())));
    }

    @DeleteMapping("/api/timetable/{entryId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEntry(@PathVariable String entryId) {
        timetableService.deleteEntry(schoolContextService.getSchoolId(), entryId);
        return ResponseEntity.ok(ApiResponse.success("Entry deleted"));
    }
}
