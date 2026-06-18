package com.school.school_app.dto.response;

import com.school.school_app.entity.Homework;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkResponse {

    private String id;
    private String schoolId;
    private String sectionId;
    private String sectionName;
    private String classRoomId;
    private String classRoomName;
    private String title;
    private String description;
    private String subject;
    private LocalDate dueDate;
    private String assignedById;
    private String assignedByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static HomeworkResponse from(Homework hw) {
        return HomeworkResponse.builder()
                .id(hw.getId())
                .schoolId(hw.getSchoolId())
                .sectionId(hw.getSectionId())
                .sectionName(hw.getSectionName())
                .classRoomId(hw.getClassRoomId())
                .classRoomName(hw.getClassRoomName())
                .title(hw.getTitle())
                .description(hw.getDescription())
                .subject(hw.getSubject())
                .dueDate(hw.getDueDate())
                .assignedById(hw.getAssignedById())
                .assignedByName(hw.getAssignedByName())
                .createdAt(hw.getCreatedAt())
                .updatedAt(hw.getUpdatedAt())
                .build();
    }
}
