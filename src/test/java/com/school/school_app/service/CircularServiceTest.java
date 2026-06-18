package com.school.school_app.service;

import com.school.school_app.dto.request.CreateCircularRequest;
import com.school.school_app.dto.response.CircularResponse;
import com.school.school_app.entity.Circular;
import com.school.school_app.entity.CircularRead;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.CircularReadRepository;
import com.school.school_app.repository.CircularRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CircularServiceTest {

    @Mock CircularRepository circularRepository;
    @Mock CircularReadRepository circularReadRepository;
    @InjectMocks CircularService circularService;

    private static final String SCHOOL_ID = "school1";
    private static final String USER_ID = "user1";

    private Circular sampleCircular() {
        return Circular.builder().id("c1").schoolId(SCHOOL_ID).schoolName("Test School")
                .title("Test Circular").content("Content").targetType("ALL")
                .publishedById(USER_ID).publishedByName("Admin").publishedAt(LocalDateTime.now()).build();
    }

    @BeforeEach
    void setUp() {}

    @Test
    void publish_savesAndReturnsResponse() {
        CreateCircularRequest req = new CreateCircularRequest();
        req.setTitle("New Circular"); req.setContent("Body"); req.setTargetType("ALL");
        Circular saved = sampleCircular();
        when(circularRepository.save(any())).thenReturn(saved);
        CircularResponse res = circularService.publish(SCHOOL_ID, "School", req, USER_ID, "Admin");
        assertThat(res.getTitle()).isEqualTo("Test Circular");
        assertThat(res.isRead()).isFalse();
    }

    @Test
    void getAll_marksReadStatus() {
        Circular c = sampleCircular();
        CircularRead cr = CircularRead.builder().circularId("c1").userId(USER_ID).build();
        when(circularRepository.findBySchoolIdOrderByPublishedAtDesc(SCHOOL_ID)).thenReturn(List.of(c));
        when(circularReadRepository.findByUserId(USER_ID)).thenReturn(List.of(cr));
        List<CircularResponse> list = circularService.getAll(SCHOOL_ID, USER_ID);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).isRead()).isTrue();
    }

    @Test
    void getById_found_returnsResponse() {
        when(circularRepository.findByIdAndSchoolId("c1", SCHOOL_ID)).thenReturn(Optional.of(sampleCircular()));
        when(circularReadRepository.existsByCircularIdAndUserId("c1", USER_ID)).thenReturn(false);
        CircularResponse res = circularService.getById(SCHOOL_ID, "c1", USER_ID);
        assertThat(res.getId()).isEqualTo("c1");
        assertThat(res.isRead()).isFalse();
    }

    @Test
    void getById_notFound_throwsException() {
        when(circularRepository.findByIdAndSchoolId("bad", SCHOOL_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> circularService.getById(SCHOOL_ID, "bad", USER_ID))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void update_ownCircular_updates() {
        Circular c = sampleCircular();
        CreateCircularRequest req = new CreateCircularRequest();
        req.setTitle("Updated"); req.setContent("New"); req.setTargetType("ALL");
        when(circularRepository.findByIdAndSchoolId("c1", SCHOOL_ID)).thenReturn(Optional.of(c));
        when(circularRepository.save(any())).thenReturn(c);
        when(circularReadRepository.existsByCircularIdAndUserId(any(), any())).thenReturn(false);
        CircularResponse res = circularService.update(SCHOOL_ID, "c1", req, USER_ID);
        assertThat(res).isNotNull();
    }

    @Test
    void update_notOwner_throwsForbidden() {
        Circular c = sampleCircular();
        when(circularRepository.findByIdAndSchoolId("c1", SCHOOL_ID)).thenReturn(Optional.of(c));
        CreateCircularRequest req = new CreateCircularRequest();
        req.setTitle("X"); req.setContent("X"); req.setTargetType("ALL");
        assertThatThrownBy(() -> circularService.update(SCHOOL_ID, "c1", req, "otherUser"))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void delete_removesCircular() {
        when(circularRepository.findByIdAndSchoolId("c1", SCHOOL_ID)).thenReturn(Optional.of(sampleCircular()));
        circularService.delete(SCHOOL_ID, "c1");
        verify(circularRepository).delete(any());
    }

    @Test
    void markRead_createsNewRecord() {
        when(circularReadRepository.findByCircularIdAndUserId("c1", USER_ID)).thenReturn(Optional.empty());
        when(circularReadRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        circularService.markRead("c1", USER_ID);
        verify(circularReadRepository).save(any());
    }

    @Test
    void getUnreadCount_correctCount() {
        CircularRead cr = CircularRead.builder().circularId("c1").userId(USER_ID).build();
        when(circularReadRepository.findByUserId(USER_ID)).thenReturn(List.of(cr));
        Circular c1 = sampleCircular(); // c1 is read
        Circular c2 = Circular.builder().id("c2").schoolId(SCHOOL_ID).build(); // c2 unread
        when(circularRepository.findBySchoolIdOrderByPublishedAtDesc(SCHOOL_ID)).thenReturn(List.of(c1, c2));
        assertThat(circularService.getUnreadCount(SCHOOL_ID, USER_ID)).isEqualTo(1);
    }
}
