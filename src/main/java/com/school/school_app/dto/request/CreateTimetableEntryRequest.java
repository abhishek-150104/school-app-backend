package com.school.school_app.dto.request;

import lombok.Data;

@Data
public class CreateTimetableEntryRequest {
    private String sectionId;
    private String subjectId;
    private String teacherId;
    private String dayOfWeek;
    private int periodNumber;
    private String startTime;
    private String endTime;
}
