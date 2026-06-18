package com.school.school_app.dto.request;

import lombok.Data;

@Data
public class CreateNotificationRequest {
    private String recipientId;
    private String recipientRole;
    private String title;
    private String body;
    private String type;
    private String referenceId;
    private String referenceType;
}
