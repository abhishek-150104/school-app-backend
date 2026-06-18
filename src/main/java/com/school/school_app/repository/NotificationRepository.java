package com.school.school_app.repository;

import com.school.school_app.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findBySchoolIdAndRecipientIdOrderByCreatedAtDesc(String schoolId, String recipientId);
    List<Notification> findBySchoolIdAndRecipientIdAndRead(String schoolId, String recipientId, boolean read);
    long countBySchoolIdAndRecipientIdAndRead(String schoolId, String recipientId, boolean read);
}
