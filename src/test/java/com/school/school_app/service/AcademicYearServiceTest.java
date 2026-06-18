package com.school.school_app.service;

import com.school.school_app.dto.request.CreateAcademicYearRequest;
import com.school.school_app.dto.response.AcademicYearResponse;
import com.school.school_app.entity.AcademicYear;
import com.school.school_app.entity.School;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.AcademicYearRepository;
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
class AcademicYearServiceTest {

    @Mock AcademicYearRepository academicYearRepository;
    @Mock SchoolService schoolService;

    @InjectMocks AcademicYearService academicYearService;

    private School testSchool;
    private AcademicYear testYear;

    @BeforeEach
    void setUp() {
        testSchool = School.builder().id("school-1").name("Test School").build();
        testYear = AcademicYear.builder()
                .id("year-1")
                .schoolId("school-1")
                .label("2024-25")
                .startYear(2024)
                .endYear(2025)
                .active(false)
                .build();
    }

    @Test
    void create_withValidData_shouldReturnAcademicYearResponse() {
        CreateAcademicYearRequest req = new CreateAcademicYearRequest();
        req.setLabel("2024-25");
        req.setStartYear(2024);
        req.setEndYear(2025);

        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(academicYearRepository.existsBySchoolIdAndLabel("school-1", "2024-25")).thenReturn(false);
        when(academicYearRepository.save(any())).thenReturn(testYear);

        AcademicYearResponse result = academicYearService.create("school-1", req);

        assertThat(result.getLabel()).isEqualTo("2024-25");
        assertThat(result.getSchoolId()).isEqualTo("school-1");
    }

    @Test
    void create_withDuplicateLabel_shouldThrowConflict() {
        CreateAcademicYearRequest req = new CreateAcademicYearRequest();
        req.setLabel("2024-25");
        req.setStartYear(2024);
        req.setEndYear(2025);

        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(academicYearRepository.existsBySchoolIdAndLabel("school-1", "2024-25")).thenReturn(true);

        assertThatThrownBy(() -> academicYearService.create("school-1", req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void create_withEndBeforeStart_shouldThrowBadRequest() {
        CreateAcademicYearRequest req = new CreateAcademicYearRequest();
        req.setLabel("2025-24");
        req.setStartYear(2025);
        req.setEndYear(2024);

        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(academicYearRepository.existsBySchoolIdAndLabel(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> academicYearService.create("school-1", req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void create_whenActive_shouldDeactivateExistingYears() {
        CreateAcademicYearRequest req = new CreateAcademicYearRequest();
        req.setLabel("2025-26");
        req.setStartYear(2025);
        req.setEndYear(2026);
        req.setActive(true);

        AcademicYear existingActive = AcademicYear.builder().id("old-year").active(true).build();
        AcademicYear newYear = AcademicYear.builder().id("new-year").label("2025-26").active(true)
                .schoolId("school-1").startYear(2025).endYear(2026).build();

        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(academicYearRepository.existsBySchoolIdAndLabel(any(), any())).thenReturn(false);
        when(academicYearRepository.findBySchoolId("school-1")).thenReturn(List.of(existingActive));
        when(academicYearRepository.save(any())).thenReturn(newYear);

        academicYearService.create("school-1", req);

        assertThat(existingActive.isActive()).isFalse();
        verify(academicYearRepository).saveAll(List.of(existingActive));
    }

    @Test
    void getAllBySchool_shouldReturnList() {
        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(academicYearRepository.findBySchoolIdOrderByStartYearDesc("school-1"))
                .thenReturn(List.of(testYear));

        List<AcademicYearResponse> result = academicYearService.getAllBySchool("school-1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("year-1");
    }

    @Test
    void getActive_withActiveYear_shouldReturn() {
        testYear.setActive(true);
        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(academicYearRepository.findBySchoolIdAndActiveTrue("school-1"))
                .thenReturn(Optional.of(testYear));

        AcademicYearResponse result = academicYearService.getActive("school-1");

        assertThat(result.isActive()).isTrue();
    }

    @Test
    void getActive_withNoActiveYear_shouldThrowNotFound() {
        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(academicYearRepository.findBySchoolIdAndActiveTrue("school-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> academicYearService.getActive("school-1"))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void activate_shouldDeactivateOthersAndSetActive() {
        AcademicYear other = AcademicYear.builder().id("other").schoolId("school-1").active(true).build();

        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(academicYearRepository.findById("year-1")).thenReturn(Optional.of(testYear));
        when(academicYearRepository.findBySchoolId("school-1")).thenReturn(List.of(other, testYear));
        when(academicYearRepository.save(any())).thenReturn(testYear);

        academicYearService.activate("school-1", "year-1");

        assertThat(other.isActive()).isFalse();
        assertThat(testYear.isActive()).isTrue();
    }

    @Test
    void delete_shouldRemoveYear() {
        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(academicYearRepository.findById("year-1")).thenReturn(Optional.of(testYear));

        academicYearService.delete("school-1", "year-1");

        verify(academicYearRepository).delete(testYear);
    }

    @Test
    void findByIdAndSchool_withWrongSchool_shouldThrowNotFound() {
        when(academicYearRepository.findById("year-1")).thenReturn(Optional.of(testYear));

        assertThatThrownBy(() -> academicYearService.findByIdAndSchool("year-1", "different-school"))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
