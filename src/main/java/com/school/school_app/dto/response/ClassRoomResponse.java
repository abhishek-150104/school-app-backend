package com.school.school_app.dto.response;

import com.school.school_app.entity.ClassRoom;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ClassRoomResponse {

    private Long id;
    private Long schoolId;
    private Long academicYearId;
    private String academicYearLabel;
    private String name;
    private int displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ClassRoomResponse from(ClassRoom classRoom) {
        return ClassRoomResponse.builder()
                .id(classRoom.getId())
                .schoolId(classRoom.getSchool().getId())
                .academicYearId(classRoom.getAcademicYear().getId())
                .academicYearLabel(classRoom.getAcademicYear().getLabel())
                .name(classRoom.getName())
                .displayOrder(classRoom.getDisplayOrder())
                .createdAt(classRoom.getCreatedAt())
                .updatedAt(classRoom.getUpdatedAt())
                .build();
    }
}
