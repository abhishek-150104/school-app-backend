package com.school.school_app.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
@CompoundIndexes({
        @CompoundIndex(def = "{'schoolId': 1, 'recipientId': 1, 'createdAt': -1}"),
        @CompoundIndex(def = "{'schoolId': 1, 'read': 1}")
})
public class Notification {

    @Id
    private String id;

    private String schoolId;

    private String recipientId;
    private String recipientRole;

    private String title;
    private String body;
    private String type; // HOMEWORK, CIRCULAR, FEE, EXAM, GENERAL

    private String referenceId;
    private String referenceType;

    private boolean read;

    @CreatedDate
    private LocalDateTime createdAt;
}
