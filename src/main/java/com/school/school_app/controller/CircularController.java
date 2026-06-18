package com.school.school_app.controller;

import com.school.school_app.dto.request.CreateCircularRequest;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.dto.response.CircularResponse;
import com.school.school_app.entity.User;
import com.school.school_app.service.CircularService;
import com.school.school_app.service.SchoolContextService;
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
public class CircularController {

    private final CircularService circularService;
    private final SchoolContextService schoolContextService;

    @PostMapping("/api/circulars")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<CircularResponse>> publish(
            @Valid @RequestBody CreateCircularRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Circular published",
                circularService.publish(schoolContextService.getSchoolId(),
                        schoolContextService.getSchoolName(), request,
                        currentUser.getId(), currentUser.getFullName())));
    }

    @GetMapping("/api/circulars/unread-count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Integer>> getUnreadCount(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                circularService.getUnreadCount(schoolContextService.getSchoolId(), currentUser.getId())));
    }

    @GetMapping("/api/circulars")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<CircularResponse>>> getAll(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                circularService.getAll(schoolContextService.getSchoolId(), currentUser.getId())));
    }

    @GetMapping("/api/circulars/{circularId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CircularResponse>> getById(
            @PathVariable String circularId,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                circularService.getById(schoolContextService.getSchoolId(), circularId, currentUser.getId())));
    }

    @PutMapping("/api/circulars/{circularId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<CircularResponse>> update(
            @PathVariable String circularId,
            @Valid @RequestBody CreateCircularRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Circular updated",
                circularService.update(schoolContextService.getSchoolId(), circularId, request, currentUser.getId())));
    }

    @DeleteMapping("/api/circulars/{circularId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String circularId) {
        circularService.delete(schoolContextService.getSchoolId(), circularId);
        return ResponseEntity.ok(ApiResponse.success("Circular deleted"));
    }

    @PostMapping("/api/circulars/{circularId}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> markRead(
            @PathVariable String circularId,
            @AuthenticationPrincipal User currentUser) {
        circularService.markRead(circularId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Marked as read"));
    }
}
