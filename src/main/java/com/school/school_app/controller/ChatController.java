package com.school.school_app.controller;

import com.school.school_app.dto.request.CreateChannelRequest;
import com.school.school_app.dto.request.SendMessageRequest;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.dto.response.ChatChannelResponse;
import com.school.school_app.dto.response.ChatMessageResponse;
import com.school.school_app.entity.User;
import com.school.school_app.service.ChatService;
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
public class ChatController {

    private final ChatService chatService;
    private final SchoolContextService schoolContextService;

    @PostMapping("/api/chat/channels")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ChatChannelResponse>> createChannel(
            @RequestBody CreateChannelRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Channel created",
                chatService.createChannel(schoolContextService.getSchoolId(), request,
                        currentUser.getId(), currentUser.getFullName())));
    }

    @GetMapping("/api/chat/channels")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ChatChannelResponse>>> getMyChannels(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                chatService.getMyChannels(schoolContextService.getSchoolId(), currentUser.getId())));
    }

    @GetMapping("/api/chat/channels/group")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ChatChannelResponse>>> getGroupChannels() {
        return ResponseEntity.ok(ApiResponse.success(
                chatService.getGroupChannels(schoolContextService.getSchoolId())));
    }

    @PostMapping("/api/chat/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ChatMessageResponse>> sendMessage(
            @RequestBody SendMessageRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Message sent",
                chatService.sendMessage(schoolContextService.getSchoolId(), request,
                        currentUser.getId(), currentUser.getFullName(),
                        currentUser.getRole().name())));
    }

    @GetMapping("/api/chat/channels/{channelId}/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getChannelMessages(
            @PathVariable String channelId,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                chatService.getChannelMessages(schoolContextService.getSchoolId(),
                        channelId, currentUser.getId())));
    }
}
