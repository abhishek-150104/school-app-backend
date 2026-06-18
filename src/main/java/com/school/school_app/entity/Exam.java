package com.school.school_app.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "exams")
@CompoundIndexes({
        @CompoundIndex(def = "{'schoolId': 1, 'academicYearId': 1, 'title': 1}", unique = true),
        @CompoundIndex(def = "{'schoolId': 1, 'classRoomId': 1, 'startDate': -1}")
})
public class Exam {

    @Id
    private String id;

    private String schoolId;
    private String schoolName;

    private String academicYearId;
    private String academicYearName;

    private String classRoomId;
    private String classRoomName;

    private String title;
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    private String status; // UPCOMING, ONGOING, COMPLETED

    private String createdById;
    private String createdByName;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
