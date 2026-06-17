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
@Document(collection = "attendance")
@CompoundIndexes({
        @CompoundIndex(def = "{'schoolId': 1, 'sectionId': 1, 'date': 1, 'studentId': 1}", unique = true),
        @CompoundIndex(def = "{'schoolId': 1, 'sectionId': 1, 'date': 1}")
})
public class AttendanceRecord {

    @Id
    private String id;

    private String schoolId;
    private String classRoomId;
    private String classRoomName;
    private String sectionId;
    private String sectionName;

    private LocalDate date;

    private String studentId;
    private String studentFullName;
    private String admissionNumber;

    private AttendanceStatus status;

    private String markedById;
    private String markedByName;

    private String remarks;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
