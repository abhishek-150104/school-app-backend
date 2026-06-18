package com.school.school_app.dto.response;

import com.school.school_app.entity.Section;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SectionResponse {

    private String id;
    private String classRoomId;
    private String classRoomName;
    private String name;
    private int capacity;
    private String classTeacherId;
    private String classTeacherName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SectionResponse from(Section section) {
        return SectionResponse.builder()
                .id(section.getId())
                .classRoomId(section.getClassRoomId())
                .classRoomName(section.getClassRoomName())
                .name(section.getName())
                .capacity(section.getCapacity())
                .classTeacherId(section.getClassTeacherId())
                .classTeacherName(section.getClassTeacherName())
                .createdAt(section.getCreatedAt())
                .updatedAt(section.getUpdatedAt())
                .build();
    }
}
