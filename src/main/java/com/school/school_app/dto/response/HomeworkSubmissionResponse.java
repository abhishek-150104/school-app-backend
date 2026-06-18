package com.school.school_app.dto.response;

import com.school.school_app.entity.HomeworkSubmission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkSubmissionResponse {

    private String id;
    private String homeworkId;
    private String studentId;
    private String studentFullName;
    private String admissionNumber;
    private String schoolId;
    private String sectionId;
    private LocalDateTime submittedAt;
    private String remarks;
    private LocalDateTime createdAt;

    public static HomeworkSubmissionResponse from(HomeworkSubmission s) {
        return HomeworkSubmissionResponse.builder()
                .id(s.getId())
                .homeworkId(s.getHomeworkId())
                .studentId(s.getStudentId())
                .studentFullName(s.getStudentFullName())
                .admissionNumber(s.getAdmissionNumber())
                .schoolId(s.getSchoolId())
                .sectionId(s.getSectionId())
                .submittedAt(s.getSubmittedAt())
                .remarks(s.getRemarks())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
