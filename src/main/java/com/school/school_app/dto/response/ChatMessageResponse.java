package com.school.school_app.dto.response;

import com.school.school_app.entity.ChatMessage;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageResponse {
    private String id;
    private String channelId;
    private String schoolId;
    private String senderId;
    private String senderName;
    private String senderRole;
    private String content;
    private String type;
    private LocalDateTime createdAt;

    public static ChatMessageResponse from(ChatMessage m) {
        return ChatMessageResponse.builder()
                .id(m.getId()).channelId(m.getChannelId()).schoolId(m.getSchoolId())
                .senderId(m.getSenderId()).senderName(m.getSenderName()).senderRole(m.getSenderRole())
                .content(m.getContent()).type(m.getType())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
