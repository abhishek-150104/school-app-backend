package com.school.school_app.service;

import com.school.school_app.dto.request.CreateNotificationRequest;
import com.school.school_app.dto.response.NotificationResponse;
import com.school.school_app.entity.Notification;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock NotificationRepository notificationRepo;

    @InjectMocks NotificationService notificationService;

    private static final String SCHOOL_ID = "school1";

    @Test
    void createNotification_success() {
        CreateNotificationRequest req = new CreateNotificationRequest();
        req.setRecipientId("u1");
        req.setRecipientRole("STUDENT");
        req.setTitle("Homework Due");
        req.setBody("Submit homework by tomorrow");
        req.setType("HOMEWORK");

        Notification saved = Notification.builder().id("n1").title("Homework Due").read(false).build();
        when(notificationRepo.save(any())).thenReturn(saved);

        NotificationResponse result = notificationService.createNotification(SCHOOL_ID, req);

        assertThat(result.getId()).isEqualTo("n1");
        assertThat(result.isRead()).isFalse();
    }

    @Test
    void getMyNotifications_returnsList() {
        Notification n = Notification.builder().id("n1").recipientId("u1").schoolId(SCHOOL_ID).build();
        when(notificationRepo.findBySchoolIdAndRecipientIdOrderByCreatedAtDesc(SCHOOL_ID, "u1"))
                .thenReturn(List.of(n));

        List<NotificationResponse> result = notificationService.getMyNotifications(SCHOOL_ID, "u1");

        assertThat(result).hasSize(1);
    }

    @Test
    void getUnreadNotifications_returnsUnread() {
        Notification n = Notification.builder().id("n1").read(false).build();
        when(notificationRepo.findBySchoolIdAndRecipientIdAndRead(SCHOOL_ID, "u1", false))
                .thenReturn(List.of(n));

        List<NotificationResponse> result = notificationService.getUnreadNotifications(SCHOOL_ID, "u1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isRead()).isFalse();
    }

    @Test
    void getUnreadCount_returnsCount() {
        when(notificationRepo.countBySchoolIdAndRecipientIdAndRead(SCHOOL_ID, "u1", false))
                .thenReturn(3L);

        long count = notificationService.getUnreadCount(SCHOOL_ID, "u1");

        assertThat(count).isEqualTo(3L);
    }

    @Test
    void markAsRead_success() {
        Notification n = Notification.builder().id("n1").schoolId(SCHOOL_ID)
                .recipientId("u1").read(false).build();
        when(notificationRepo.findById("n1")).thenReturn(Optional.of(n));
        when(notificationRepo.save(any())).thenReturn(n);

        NotificationResponse result = notificationService.markAsRead(SCHOOL_ID, "n1", "u1");

        assertThat(result.isRead()).isTrue();
        verify(notificationRepo).save(argThat(notif -> notif.isRead()));
    }

    @Test
    void markAsRead_wrongUserThrows() {
        Notification n = Notification.builder().id("n1").schoolId(SCHOOL_ID)
                .recipientId("u2").read(false).build();
        when(notificationRepo.findById("n1")).thenReturn(Optional.of(n));

        assertThatThrownBy(() -> notificationService.markAsRead(SCHOOL_ID, "n1", "u1"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Notification not found");
    }

    @Test
    void markAllAsRead_savesAll() {
        Notification n1 = Notification.builder().id("n1").read(false).build();
        Notification n2 = Notification.builder().id("n2").read(false).build();
        when(notificationRepo.findBySchoolIdAndRecipientIdAndRead(SCHOOL_ID, "u1", false))
                .thenReturn(List.of(n1, n2));

        notificationService.markAllAsRead(SCHOOL_ID, "u1");

        verify(notificationRepo).saveAll(argThat(list -> {
            List<Notification> l = (List<Notification>) list;
            return l.size() == 2 && l.stream().allMatch(Notification::isRead);
        }));
    }

    @Test
    void markAllAsRead_emptyDoesNotSave() {
        when(notificationRepo.findBySchoolIdAndRecipientIdAndRead(SCHOOL_ID, "u1", false))
                .thenReturn(List.of());

        notificationService.markAllAsRead(SCHOOL_ID, "u1");

        verify(notificationRepo).saveAll(argThat(list -> ((List<?>) list).isEmpty()));
    }
}
