package com.school.school_app.dto.response;

import com.school.school_app.entity.Exam;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ExamResponse {
    private String id;
    private String academicYearName;
    private String classRoomId;
    private String classRoomName;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private LocalDateTime createdAt;

    public static ExamResponse from(Exam e) {
        return ExamResponse.builder()
                .id(e.getId())
                .academicYearName(e.getAcademicYearName())
                .classRoomId(e.getClassRoomId()).classRoomName(e.getClassRoomName())
                .title(e.getTitle()).description(e.getDescription())
                .startDate(e.getStartDate()).endDate(e.getEndDate())
                .status(e.getStatus()).createdAt(e.getCreatedAt())
                .build();
    }
}
