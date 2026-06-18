package com.school.school_app.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_channels")
@CompoundIndexes({
        @CompoundIndex(def = "{'schoolId': 1, 'type': 1}"),
        @CompoundIndex(def = "{'schoolId': 1, 'members': 1}")
})
public class ChatChannel {

    @Id
    private String id;

    private String schoolId;
    private String name;
    private String type; // GROUP, DIRECT

    private List<String> members;
    private List<String> memberNames;

    private String createdById;
    private String createdByName;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
