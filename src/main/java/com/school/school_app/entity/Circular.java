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
@Document(collection = "circulars")
@CompoundIndexes({
    @CompoundIndex(def = "{'schoolId': 1, 'publishedAt': -1}"),
    @CompoundIndex(def = "{'schoolId': 1, 'targetType': 1}")
})
public class Circular {
    @Id private String id;
    private String schoolId, schoolName, title, content;
    private String targetType; // ALL, CLASS, SECTION
    private String targetClassRoomId, targetClassRoomName;
    private String targetSectionId, targetSectionName;
    private String publishedById, publishedByName;
    private LocalDateTime publishedAt;
    @CreatedDate private LocalDateTime createdAt;
    @LastModifiedDate private LocalDateTime updatedAt;
}
