package com.school.school_app.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_messages")
@CompoundIndexes({
        @CompoundIndex(def = "{'channelId': 1, 'createdAt': -1}")
})
public class ChatMessage {

    @Id
    private String id;

    private String channelId;
    private String schoolId;

    private String senderId;
    private String senderName;
    private String senderRole;

    private String content;
    private String type; // TEXT, IMAGE, FILE

    @CreatedDate
    private LocalDateTime createdAt;
}
