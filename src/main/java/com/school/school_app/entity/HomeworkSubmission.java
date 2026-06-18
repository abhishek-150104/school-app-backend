package com.school.school_app.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "homework_submissions")
@CompoundIndex(def = "{'homeworkId': 1, 'studentId': 1}", unique = true)
public class HomeworkSubmission {

    @Id
    private String id;

    private String homeworkId;
    private String studentId;
    private String studentFullName;
    private String admissionNumber;
    private String schoolId;
    private String sectionId;
    private LocalDateTime submittedAt;
    private String remarks;

    @CreatedDate
    private LocalDateTime createdAt;
}
