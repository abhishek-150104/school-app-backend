package com.school.school_app.dto.response;

import com.school.school_app.entity.Section;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SectionResponse {

    private Long id;
    private Long classRoomId;
    private String classRoomName;
    private String name;
    private int capacity;
    private Long classTeacherId;
    private String classTeacherName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SectionResponse from(Section section) {
        return SectionResponse.builder()
                .id(section.getId())
                .classRoomId(section.getClassRoom().getId())
                .classRoomName(section.getClassRoom().getName())
                .name(section.getName())
                .capacity(section.getCapacity())
                .classTeacherId(section.getClassTeacher() != null ? section.getClassTeacher().getId() : null)
                .classTeacherName(section.getClassTeacher() != null ? section.getClassTeacher().getFullName() : null)
                .createdAt(section.getCreatedAt())
                .updatedAt(section.getUpdatedAt())
                .build();
    }
}
