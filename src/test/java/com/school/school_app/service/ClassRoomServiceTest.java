package com.school.school_app.service;

import com.school.school_app.dto.request.CreateClassRoomRequest;
import com.school.school_app.dto.request.UpdateClassRoomRequest;
import com.school.school_app.dto.response.ClassRoomResponse;
import com.school.school_app.entity.AcademicYear;
import com.school.school_app.entity.ClassRoom;
import com.school.school_app.entity.School;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.ClassRoomRepository;
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
class ClassRoomServiceTest {

    @Mock ClassRoomRepository classRoomRepository;
    @Mock SchoolService schoolService;
    @Mock AcademicYearService academicYearService;

    @InjectMocks ClassRoomService classRoomService;

    private School testSchool;
    private AcademicYear testYear;
    private ClassRoom testClass;

    @BeforeEach
    void setUp() {
        testSchool = School.builder().id("school-1").name("Test School").build();
        testYear = AcademicYear.builder().id("year-1").schoolId("school-1").label("2024-25").build();
        testClass = ClassRoom.builder()
                .id("class-1")
                .schoolId("school-1")
                .academicYearId("year-1")
                .academicYearLabel("2024-25")
                .name("Class 5")
                .displayOrder(5)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void create_withValidData_shouldReturnClassRoomResponse() {
        CreateClassRoomRequest req = new CreateClassRoomRequest();
        req.setName("Class 5");
        req.setAcademicYearId("year-1");

        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(academicYearService.findByIdAndSchool("year-1", "school-1")).thenReturn(testYear);
        when(classRoomRepository.existsBySchoolIdAndAcademicYearIdAndName("school-1", "year-1", "Class 5"))
                .thenReturn(false);
        when(classRoomRepository.save(any())).thenReturn(testClass);

        ClassRoomResponse result = classRoomService.create("school-1", req);

        assertThat(result.getId()).isEqualTo("class-1");
        assertThat(result.getName()).isEqualTo("Class 5");
        assertThat(result.getAcademicYearLabel()).isEqualTo("2024-25");
    }

    @Test
    void create_withDuplicateName_shouldThrowConflict() {
        CreateClassRoomRequest req = new CreateClassRoomRequest();
        req.setName("Class 5");
        req.setAcademicYearId("year-1");

        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(academicYearService.findByIdAndSchool("year-1", "school-1")).thenReturn(testYear);
        when(classRoomRepository.existsBySchoolIdAndAcademicYearIdAndName("school-1", "year-1", "Class 5"))
                .thenReturn(true);

        assertThatThrownBy(() -> classRoomService.create("school-1", req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void getAllBySchool_withAcademicYearId_shouldFilterByYear() {
        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(classRoomRepository.findBySchoolIdAndAcademicYearIdOrderByDisplayOrderAscNameAsc("school-1", "year-1"))
                .thenReturn(List.of(testClass));

        List<ClassRoomResponse> result = classRoomService.getAllBySchool("school-1", "year-1");

        assertThat(result).hasSize(1);
        verify(classRoomRepository).findBySchoolIdAndAcademicYearIdOrderByDisplayOrderAscNameAsc("school-1", "year-1");
    }

    @Test
    void getAllBySchool_withoutAcademicYearId_shouldReturnAll() {
        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(classRoomRepository.findBySchoolIdOrderByDisplayOrderAscNameAsc("school-1"))
                .thenReturn(List.of(testClass));

        List<ClassRoomResponse> result = classRoomService.getAllBySchool("school-1", null);

        assertThat(result).hasSize(1);
        verify(classRoomRepository).findBySchoolIdOrderByDisplayOrderAscNameAsc("school-1");
    }

    @Test
    void getById_withValidId_shouldReturn() {
        when(classRoomRepository.findByIdAndSchoolId("class-1", "school-1"))
                .thenReturn(Optional.of(testClass));

        ClassRoomResponse result = classRoomService.getById("school-1", "class-1");

        assertThat(result.getId()).isEqualTo("class-1");
    }

    @Test
    void getById_withInvalidId_shouldThrowNotFound() {
        when(classRoomRepository.findByIdAndSchoolId("bad-id", "school-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classRoomService.getById("school-1", "bad-id"))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void update_shouldUpdateNameAndDisplayOrder() {
        UpdateClassRoomRequest req = new UpdateClassRoomRequest();
        req.setName("Class 6");
        req.setDisplayOrder(6);

        when(classRoomRepository.findByIdAndSchoolId("class-1", "school-1"))
                .thenReturn(Optional.of(testClass));
        when(classRoomRepository.save(any())).thenReturn(testClass);

        classRoomService.update("school-1", "class-1", req);

        assertThat(testClass.getName()).isEqualTo("Class 6");
        assertThat(testClass.getDisplayOrder()).isEqualTo(6);
    }

    @Test
    void delete_shouldRemoveClass() {
        when(classRoomRepository.findByIdAndSchoolId("class-1", "school-1"))
                .thenReturn(Optional.of(testClass));

        classRoomService.delete("school-1", "class-1");

        verify(classRoomRepository).delete(testClass);
    }
}
