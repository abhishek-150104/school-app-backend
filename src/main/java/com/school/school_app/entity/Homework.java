package com.school.school_app.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "homework")
@CompoundIndex(def = "{'schoolId': 1, 'sectionId': 1, 'dueDate': 1}")
public class Homework {

    @Id
    private String id;

    private String schoolId;
    private String sectionId;
    private String sectionName;
    private String classRoomId;
    private String classRoomName;

    private String title;
    private String description;
    private String subject;
    private LocalDate dueDate;

    private String assignedById;
    private String assignedByName;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
