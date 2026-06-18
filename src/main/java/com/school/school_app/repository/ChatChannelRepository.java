package com.school.school_app.repository;

import com.school.school_app.entity.ChatChannel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChatChannelRepository extends MongoRepository<ChatChannel, String> {
    List<ChatChannel> findBySchoolIdAndMembersContaining(String schoolId, String userId);
    Optional<ChatChannel> findBySchoolIdAndTypeAndMembersContainingAndMembersContaining(
            String schoolId, String type, String userId1, String userId2);
    List<ChatChannel> findBySchoolIdAndType(String schoolId, String type);
}
