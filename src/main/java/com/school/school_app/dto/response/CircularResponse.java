package com.school.school_app.dto.response;

import com.school.school_app.entity.Circular;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CircularResponse {
    private String id, schoolId, schoolName, title, content;
    private String targetType, targetClassRoomId, targetClassRoomName;
    private String targetSectionId, targetSectionName;
    private String publishedById, publishedByName;
    private LocalDateTime publishedAt, createdAt, updatedAt;
    private boolean read;

    public static CircularResponse from(Circular c, boolean read) {
        return CircularResponse.builder()
                .id(c.getId()).schoolId(c.getSchoolId()).schoolName(c.getSchoolName())
                .title(c.getTitle()).content(c.getContent()).targetType(c.getTargetType())
                .targetClassRoomId(c.getTargetClassRoomId()).targetClassRoomName(c.getTargetClassRoomName())
                .targetSectionId(c.getTargetSectionId()).targetSectionName(c.getTargetSectionName())
                .publishedById(c.getPublishedById()).publishedByName(c.getPublishedByName())
                .publishedAt(c.getPublishedAt()).createdAt(c.getCreatedAt()).updatedAt(c.getUpdatedAt())
                .read(read).build();
    }
}
