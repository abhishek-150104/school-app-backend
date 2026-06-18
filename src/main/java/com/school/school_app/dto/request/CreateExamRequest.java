package com.school.school_app.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateExamRequest {
    private String academicYearId;
    private String classRoomId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
}
