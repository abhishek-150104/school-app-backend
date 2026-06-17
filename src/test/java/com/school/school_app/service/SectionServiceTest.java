package com.school.school_app.service;

import com.school.school_app.dto.request.AssignTeacherRequest;
import com.school.school_app.dto.request.CreateSectionRequest;
import com.school.school_app.dto.request.UpdateSectionRequest;
import com.school.school_app.dto.response.SectionResponse;
import com.school.school_app.entity.ClassRoom;
import com.school.school_app.entity.Role;
import com.school.school_app.entity.Section;
import com.school.school_app.entity.User;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.SectionRepository;
import com.school.school_app.repository.UserRepository;
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
class SectionServiceTest {

    @Mock SectionRepository sectionRepository;
    @Mock ClassRoomService classRoomService;
    @Mock UserRepository userRepository;

    @InjectMocks SectionService sectionService;

    private ClassRoom testClass;
    private Section testSection;
    private User testTeacher;

    @BeforeEach
    void setUp() {
        testClass = ClassRoom.builder()
                .id("class-1")
                .schoolId("school-1")
                .name("Class 5")
                .build();

        testSection = Section.builder()
                .id("section-1")
                .classRoomId("class-1")
                .classRoomName("Class 5")
                .name("A")
                .capacity(40)
                .createdAt(LocalDateTime.now())
                .build();

        testTeacher = User.builder()
                .id("teacher-1")
                .fullName("Mr. Smith")
                .role(Role.TEACHER)
                .build();
    }

    @Test
    void create_withValidData_shouldReturnSectionResponse() {
        CreateSectionRequest req = new CreateSectionRequest();
        req.setName("A");
        req.setCapacity(40);

        when(classRoomService.findById("class-1")).thenReturn(testClass);
        when(sectionRepository.existsByClassRoomIdAndName("class-1", "A")).thenReturn(false);
        when(sectionRepository.save(any())).thenReturn(testSection);

        SectionResponse result = sectionService.create("class-1", req);

        assertThat(result.getId()).isEqualTo("section-1");
        assertThat(result.getName()).isEqualTo("A");
        assertThat(result.getClassRoomName()).isEqualTo("Class 5");
    }

    @Test
    void create_withDuplicateName_shouldThrowConflict() {
        CreateSectionRequest req = new CreateSectionRequest();
        req.setName("A");

        when(classRoomService.findById("class-1")).thenReturn(testClass);
        when(sectionRepository.existsByClassRoomIdAndName("class-1", "A")).thenReturn(true);

        assertThatThrownBy(() -> sectionService.create("class-1", req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void getAllByClass_shouldReturnOrderedList() {
        when(classRoomService.findById("class-1")).thenReturn(testClass);
        when(sectionRepository.findByClassRoomIdOrderByNameAsc("class-1"))
                .thenReturn(List.of(testSection));

        List<SectionResponse> result = sectionService.getAllByClass("class-1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("A");
    }

    @Test
    void getById_withValidId_shouldReturn() {
        when(sectionRepository.findByIdAndClassRoomId("section-1", "class-1"))
                .thenReturn(Optional.of(testSection));

        SectionResponse result = sectionService.getById("class-1", "section-1");

        assertThat(result.getId()).isEqualTo("section-1");
    }

    @Test
    void update_shouldUpdateNameAndCapacity() {
        UpdateSectionRequest req = new UpdateSectionRequest();
        req.setName("B");
        req.setCapacity(45);

        when(sectionRepository.findByIdAndClassRoomId("section-1", "class-1"))
                .thenReturn(Optional.of(testSection));
        when(sectionRepository.save(any())).thenReturn(testSection);

        sectionService.update("class-1", "section-1", req);

        assertThat(testSection.getName()).isEqualTo("B");
        assertThat(testSection.getCapacity()).isEqualTo(45);
    }

    @Test
    void assignTeacher_withValidTeacher_shouldSetTeacherOnSection() {
        AssignTeacherRequest req = new AssignTeacherRequest();
        req.setTeacherId("teacher-1");

        when(sectionRepository.findByIdAndClassRoomId("section-1", "class-1"))
                .thenReturn(Optional.of(testSection));
        when(userRepository.findById("teacher-1")).thenReturn(Optional.of(testTeacher));
        when(sectionRepository.save(any())).thenReturn(testSection);

        SectionResponse result = sectionService.assignTeacher("class-1", "section-1", req);

        assertThat(testSection.getClassTeacherId()).isEqualTo("teacher-1");
        assertThat(testSection.getClassTeacherName()).isEqualTo("Mr. Smith");
    }

    @Test
    void assignTeacher_withNonTeacherUser_shouldThrowBadRequest() {
        AssignTeacherRequest req = new AssignTeacherRequest();
        req.setTeacherId("student-1");

        User parent = User.builder().id("student-1").role(Role.PARENT).build();

        when(sectionRepository.findByIdAndClassRoomId("section-1", "class-1"))
                .thenReturn(Optional.of(testSection));
        when(userRepository.findById("student-1")).thenReturn(Optional.of(parent));

        assertThatThrownBy(() -> sectionService.assignTeacher("class-1", "section-1", req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void assignTeacher_withNonExistentUser_shouldThrowNotFound() {
        AssignTeacherRequest req = new AssignTeacherRequest();
        req.setTeacherId("ghost-id");

        when(sectionRepository.findByIdAndClassRoomId("section-1", "class-1"))
                .thenReturn(Optional.of(testSection));
        when(userRepository.findById("ghost-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sectionService.assignTeacher("class-1", "section-1", req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void delete_shouldRemoveSection() {
        when(sectionRepository.findByIdAndClassRoomId("section-1", "class-1"))
                .thenReturn(Optional.of(testSection));

        sectionService.delete("class-1", "section-1");

        verify(sectionRepository).delete(testSection);
    }
}
