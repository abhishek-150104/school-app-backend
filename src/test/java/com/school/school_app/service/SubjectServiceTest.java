package com.school.school_app.service;

import com.school.school_app.dto.request.CreateSubjectRequest;
import com.school.school_app.dto.response.SubjectResponse;
import com.school.school_app.entity.ClassRoom;
import com.school.school_app.entity.Subject;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock SubjectRepository subjectRepository;
    @Mock ClassRoomService classRoomService;

    @InjectMocks SubjectService subjectService;

    private ClassRoom classRoom;
    private Subject subject;
    private final String SCHOOL_ID = "school1";
    private final String CLASS_ID  = "class1";
    private final String SUBJECT_ID = "sub1";

    @BeforeEach
    void setUp() {
        classRoom = new ClassRoom();
        classRoom.setId(CLASS_ID);
        classRoom.setSchoolId(SCHOOL_ID);
        classRoom.setName("Class 5");

        subject = Subject.builder()
                .id(SUBJECT_ID)
                .schoolId(SCHOOL_ID)
                .classRoomId(CLASS_ID)
                .classRoomName("Class 5")
                .name("Mathematics")
                .code("MATH")
                .createdById("admin1")
                .createdByName("Admin User")
                .build();
    }

    @Test
    void create_savesSubject_whenNameIsUnique() {
        CreateSubjectRequest request = new CreateSubjectRequest();
        request.setName("Mathematics");
        request.setCode("MATH");

        when(classRoomService.findByIdAndSchool(CLASS_ID, SCHOOL_ID)).thenReturn(classRoom);
        when(subjectRepository.existsByClassRoomIdAndName(CLASS_ID, "Mathematics")).thenReturn(false);
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        SubjectResponse response = subjectService.create(SCHOOL_ID, CLASS_ID, request, "admin1", "Admin User");

        assertThat(response.getName()).isEqualTo("Mathematics");
        assertThat(response.getCode()).isEqualTo("MATH");
        assertThat(response.getClassRoomName()).isEqualTo("Class 5");
        verify(subjectRepository).save(any(Subject.class));
    }

    @Test
    void create_throwsConflict_whenNameAlreadyExists() {
        CreateSubjectRequest request = new CreateSubjectRequest();
        request.setName("Mathematics");

        when(classRoomService.findByIdAndSchool(CLASS_ID, SCHOOL_ID)).thenReturn(classRoom);
        when(subjectRepository.existsByClassRoomIdAndName(CLASS_ID, "Mathematics")).thenReturn(true);

        assertThatThrownBy(() ->
                subjectService.create(SCHOOL_ID, CLASS_ID, request, "admin1", "Admin User"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("already exists")
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(subjectRepository, never()).save(any());
    }

    @Test
    void getByClassRoom_returnsSubjectsInOrder() {
        Subject s2 = Subject.builder().id("sub2").schoolId(SCHOOL_ID)
                .classRoomId(CLASS_ID).classRoomName("Class 5")
                .name("Science").code("SCI").build();

        when(classRoomService.findByIdAndSchool(CLASS_ID, SCHOOL_ID)).thenReturn(classRoom);
        when(subjectRepository.findByClassRoomIdOrderByNameAsc(CLASS_ID))
                .thenReturn(List.of(subject, s2));

        List<SubjectResponse> result = subjectService.getByClassRoom(SCHOOL_ID, CLASS_ID);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Mathematics");
        assertThat(result.get(1).getName()).isEqualTo("Science");
    }

    @Test
    void getByClassRoom_returnsEmptyList_whenNoSubjects() {
        when(classRoomService.findByIdAndSchool(CLASS_ID, SCHOOL_ID)).thenReturn(classRoom);
        when(subjectRepository.findByClassRoomIdOrderByNameAsc(CLASS_ID)).thenReturn(List.of());

        List<SubjectResponse> result = subjectService.getByClassRoom(SCHOOL_ID, CLASS_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void delete_removesSubject_whenFound() {
        when(classRoomService.findByIdAndSchool(CLASS_ID, SCHOOL_ID)).thenReturn(classRoom);
        when(subjectRepository.findByIdAndSchoolId(SUBJECT_ID, SCHOOL_ID))
                .thenReturn(Optional.of(subject));

        subjectService.delete(SCHOOL_ID, CLASS_ID, SUBJECT_ID);

        verify(subjectRepository).delete(subject);
    }

    @Test
    void delete_throwsNotFound_whenSubjectMissing() {
        when(classRoomService.findByIdAndSchool(CLASS_ID, SCHOOL_ID)).thenReturn(classRoom);
        when(subjectRepository.findByIdAndSchoolId("bad", SCHOOL_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subjectService.delete(SCHOOL_ID, CLASS_ID, "bad"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Subject not found")
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void findByClassRoomId_delegatesToRepository() {
        when(subjectRepository.findByClassRoomIdOrderByNameAsc(CLASS_ID))
                .thenReturn(List.of(subject));

        List<Subject> result = subjectService.findByClassRoomId(CLASS_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Mathematics");
    }
}
