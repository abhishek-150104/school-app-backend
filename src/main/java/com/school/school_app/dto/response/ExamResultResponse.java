package com.school.school_app.dto.response;

import com.school.school_app.entity.ExamResult;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ExamResultResponse {
    private String id;
    private String examId;
    private String examTitle;
    private String studentId;
    private String studentFullName;
    private String admissionNumber;
    private String subjectId;
    private String subjectName;
    private double marksObtained;
    private double maxMarks;
    private double percentage;
    private String grade;
    private LocalDateTime createdAt;

    public static ExamResultResponse from(ExamResult r) {
        return ExamResultResponse.builder()
                .id(r.getId())
                .examId(r.getExamId()).examTitle(r.getExamTitle())
                .studentId(r.getStudentId()).studentFullName(r.getStudentFullName())
                .admissionNumber(r.getAdmissionNumber())
                .subjectId(r.getSubjectId()).subjectName(r.getSubjectName())
                .marksObtained(r.getMarksObtained()).maxMarks(r.getMaxMarks())
                .percentage(r.getPercentage()).grade(r.getGrade())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
