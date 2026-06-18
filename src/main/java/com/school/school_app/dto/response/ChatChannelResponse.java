package com.school.school_app.dto.response;

import com.school.school_app.entity.ChatChannel;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ChatChannelResponse {
    private String id;
    private String schoolId;
    private String name;
    private String type;
    private List<String> members;
    private List<String> memberNames;
    private String createdById;
    private String createdByName;
    private LocalDateTime createdAt;

    public static ChatChannelResponse from(ChatChannel c) {
        return ChatChannelResponse.builder()
                .id(c.getId()).schoolId(c.getSchoolId())
                .name(c.getName()).type(c.getType())
                .members(c.getMembers()).memberNames(c.getMemberNames())
                .createdById(c.getCreatedById()).createdByName(c.getCreatedByName())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
