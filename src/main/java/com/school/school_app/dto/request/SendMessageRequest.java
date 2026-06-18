package com.school.school_app.dto.request;

import lombok.Data;

@Data
public class SendMessageRequest {
    private String channelId;
    private String content;
    private String type; // TEXT
}
