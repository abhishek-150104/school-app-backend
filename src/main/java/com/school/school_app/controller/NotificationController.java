package com.school.school_app.controller;

import com.school.school_app.dto.request.CreateNotificationRequest;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.dto.response.NotificationResponse;
import com.school.school_app.entity.User;
import com.school.school_app.service.NotificationService;
import com.school.school_app.service.SchoolContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SchoolContextService schoolContextService;

    @PostMapping("/api/notifications")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(
            @RequestBody CreateNotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Notification sent",
                notificationService.createNotification(schoolContextService.getSchoolId(), request)));
    }

    @GetMapping("/api/notifications")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getMyNotifications(
                        schoolContextService.getSchoolId(), currentUser.getId())));
    }

    @GetMapping("/api/notifications/unread")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnread(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getUnreadNotifications(
                        schoolContextService.getSchoolId(), currentUser.getId())));
    }

    @GetMapping("/api/notifications/unread/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @AuthenticationPrincipal User currentUser) {
        long count = notificationService.getUnreadCount(
                schoolContextService.getSchoolId(), currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("count", count)));
    }

    @PatchMapping("/api/notifications/{notificationId}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @PathVariable String notificationId,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success("Marked as read",
                notificationService.markAsRead(
                        schoolContextService.getSchoolId(), notificationId, currentUser.getId())));
    }

    @PatchMapping("/api/notifications/read-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal User currentUser) {
        notificationService.markAllAsRead(schoolContextService.getSchoolId(), currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("All marked as read", null));
    }
}
