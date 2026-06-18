package com.school.school_app.service;

import com.school.school_app.dto.request.CreateNotificationRequest;
import com.school.school_app.dto.response.NotificationResponse;
import com.school.school_app.entity.Notification;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepo;

    public NotificationResponse createNotification(String schoolId, CreateNotificationRequest req) {
        Notification notification = Notification.builder()
                .schoolId(schoolId)
                .recipientId(req.getRecipientId())
                .recipientRole(req.getRecipientRole())
                .title(req.getTitle())
                .body(req.getBody())
                .type(req.getType())
                .referenceId(req.getReferenceId())
                .referenceType(req.getReferenceType())
                .read(false)
                .build();

        return NotificationResponse.from(notificationRepo.save(notification));
    }

    public List<NotificationResponse> getMyNotifications(String schoolId, String userId) {
        return notificationRepo.findBySchoolIdAndRecipientIdOrderByCreatedAtDesc(schoolId, userId)
                .stream().map(NotificationResponse::from).toList();
    }

    public List<NotificationResponse> getUnreadNotifications(String schoolId, String userId) {
        return notificationRepo.findBySchoolIdAndRecipientIdAndRead(schoolId, userId, false)
                .stream().map(NotificationResponse::from).toList();
    }

    public long getUnreadCount(String schoolId, String userId) {
        return notificationRepo.countBySchoolIdAndRecipientIdAndRead(schoolId, userId, false);
    }

    public NotificationResponse markAsRead(String schoolId, String notificationId, String userId) {
        Notification notification = notificationRepo.findById(notificationId)
                .filter(n -> n.getSchoolId().equals(schoolId) && n.getRecipientId().equals(userId))
                .orElseThrow(() -> new AppException("Notification not found", HttpStatus.NOT_FOUND));

        notification.setRead(true);
        return NotificationResponse.from(notificationRepo.save(notification));
    }

    public void markAllAsRead(String schoolId, String userId) {
        List<Notification> unread = notificationRepo
                .findBySchoolIdAndRecipientIdAndRead(schoolId, userId, false);
        unread.forEach(n -> n.setRead(true));
        notificationRepo.saveAll(unread);
    }
}
