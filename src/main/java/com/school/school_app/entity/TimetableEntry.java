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
@Document(collection = "timetable_entries")
@CompoundIndexes({
        @CompoundIndex(def = "{'schoolId': 1, 'sectionId': 1, 'dayOfWeek': 1, 'periodNumber': 1}", unique = true),
        @CompoundIndex(def = "{'schoolId': 1, 'teacherId': 1, 'dayOfWeek': 1}")
})
public class TimetableEntry {

    @Id
    private String id;

    private String schoolId;
    private String schoolName;

    private String academicYearId;
    private String academicYearName;

    private String classRoomId;
    private String classRoomName;
    private String sectionId;
    private String sectionName;

    private String subjectId;
    private String subjectName;

    private String teacherId;
    private String teacherName;

    private String dayOfWeek; // MONDAY .. FRIDAY
    private int periodNumber;
    private String startTime; // "08:00"
    private String endTime;   // "08:45"

    private String createdById;
    private String createdByName;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
