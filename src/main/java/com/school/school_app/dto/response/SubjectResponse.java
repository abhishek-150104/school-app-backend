package com.school.school_app.dto.response;

import com.school.school_app.entity.Subject;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SubjectResponse {

    private String id;
    private String schoolId;
    private String classRoomId;
    private String classRoomName;
    private String name;
    private String code;
    private String createdById;
    private String createdByName;
    private LocalDateTime createdAt;

    public static SubjectResponse from(Subject subject) {
        return SubjectResponse.builder()
                .id(subject.getId())
                .schoolId(subject.getSchoolId())
                .classRoomId(subject.getClassRoomId())
                .classRoomName(subject.getClassRoomName())
                .name(subject.getName())
                .code(subject.getCode())
                .createdById(subject.getCreatedById())
                .createdByName(subject.getCreatedByName())
                .createdAt(subject.getCreatedAt())
                .build();
    }
}
