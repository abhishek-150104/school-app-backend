package com.school.school_app.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "exam_results")
@CompoundIndexes({
        @CompoundIndex(def = "{'examId': 1, 'studentId': 1, 'subjectId': 1}", unique = true),
        @CompoundIndex(def = "{'schoolId': 1, 'studentId': 1, 'examId': 1}")
})
public class ExamResult {

    @Id
    private String id;

    private String schoolId;
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

    private String enteredById;
    private String enteredByName;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
