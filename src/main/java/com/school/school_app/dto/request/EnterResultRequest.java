package com.school.school_app.dto.request;

import lombok.Data;

@Data
public class EnterResultRequest {
    private String examId;
    private String studentId;
    private String subjectId;
    private double marksObtained;
    private double maxMarks;
}
