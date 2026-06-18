package com.school.school_app.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "circular_reads")
@CompoundIndex(def = "{'circularId': 1, 'userId': 1}", unique = true)
public class CircularRead {
    @Id private String id;
    private String circularId, userId;
    private LocalDateTime readAt;
}
