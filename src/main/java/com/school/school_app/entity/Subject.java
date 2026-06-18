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
@Document(collection = "subjects")
@CompoundIndex(def = "{'classRoomId': 1, 'name': 1}", unique = true)
public class Subject {

    @Id
    private String id;

    private String schoolId;
    private String classRoomId;
    private String classRoomName;

    private String name;
    private String code;

    private String createdById;
    private String createdByName;

    @CreatedDate
    private LocalDateTime createdAt;
}
