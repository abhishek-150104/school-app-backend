package com.school.school_app.repository;

import com.school.school_app.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByChannelIdOrderByCreatedAtAsc(String channelId);
    List<ChatMessage> findByChannelIdAndSchoolId(String channelId, String schoolId);
}
