package com.school.school_app.dto.response;

import com.school.school_app.entity.ClassRoom;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ClassRoomResponse {

    private String id;
    private String schoolId;
    private String academicYearId;
    private String academicYearLabel;
    private String name;
    private int displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ClassRoomResponse from(ClassRoom classRoom) {
        return ClassRoomResponse.builder()
                .id(classRoom.getId())
                .schoolId(classRoom.getSchoolId())
                .academicYearId(classRoom.getAcademicYearId())
                .academicYearLabel(classRoom.getAcademicYearLabel())
                .name(classRoom.getName())
                .displayOrder(classRoom.getDisplayOrder())
                .createdAt(classRoom.getCreatedAt())
                .updatedAt(classRoom.getUpdatedAt())
                .build();
    }
}
