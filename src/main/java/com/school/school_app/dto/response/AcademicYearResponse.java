package com.school.school_app.dto.response;

import com.school.school_app.entity.AcademicYear;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AcademicYearResponse {

    private String id;
    private String schoolId;
    private String label;
    private int startYear;
    private int endYear;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AcademicYearResponse from(AcademicYear year) {
        return AcademicYearResponse.builder()
                .id(year.getId())
                .schoolId(year.getSchoolId())
                .label(year.getLabel())
                .startYear(year.getStartYear())
                .endYear(year.getEndYear())
                .active(year.isActive())
                .createdAt(year.getCreatedAt())
                .updatedAt(year.getUpdatedAt())
                .build();
    }
}
