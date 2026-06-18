package com.school.school_app.dto.response;

import com.school.school_app.entity.Notification;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private String id;
    private String schoolId;
    private String recipientId;
    private String recipientRole;
    private String title;
    private String body;
    private String type;
    private String referenceId;
    private String referenceType;
    private boolean read;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId()).schoolId(n.getSchoolId())
                .recipientId(n.getRecipientId()).recipientRole(n.getRecipientRole())
                .title(n.getTitle()).body(n.getBody())
                .type(n.getType()).referenceId(n.getReferenceId()).referenceType(n.getReferenceType())
                .read(n.isRead()).createdAt(n.getCreatedAt())
                .build();
    }
}
