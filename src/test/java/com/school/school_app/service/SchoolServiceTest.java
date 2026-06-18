package com.school.school_app.service;

import com.school.school_app.dto.request.CreateSchoolRequest;
import com.school.school_app.dto.request.UpdateSchoolRequest;
import com.school.school_app.dto.response.SchoolResponse;
import com.school.school_app.entity.School;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.SchoolRepository;
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
class SchoolServiceTest {

    @Mock SchoolRepository schoolRepository;

    @InjectMocks SchoolService schoolService;

    private School testSchool;

    @BeforeEach
    void setUp() {
        testSchool = School.builder()
                .id("school-1")
                .name("Test School")
                .email("school@test.com")
                .phone("9876543210")
                .affiliationNumber("AFF-001")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createSchool_withValidData_shouldReturnSchoolResponse() {
        CreateSchoolRequest req = new CreateSchoolRequest();
        req.setName("Test School");
        req.setEmail("school@test.com");
        req.setPhone("9876543210");

        when(schoolRepository.existsByEmail(anyString())).thenReturn(false);
        when(schoolRepository.existsByPhone(anyString())).thenReturn(false);
        when(schoolRepository.save(any())).thenReturn(testSchool);

        SchoolResponse result = schoolService.createSchool(req);

        assertThat(result.getId()).isEqualTo("school-1");
        assertThat(result.getName()).isEqualTo("Test School");
    }

    @Test
    void createSchool_withDuplicateEmail_shouldThrowConflict() {
        CreateSchoolRequest req = new CreateSchoolRequest();
        req.setEmail("taken@test.com");

        when(schoolRepository.existsByEmail("taken@test.com")).thenReturn(true);

        assertThatThrownBy(() -> schoolService.createSchool(req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void createSchool_withDuplicatePhone_shouldThrowConflict() {
        CreateSchoolRequest req = new CreateSchoolRequest();
        req.setPhone("9876543210");

        when(schoolRepository.existsByPhone("9876543210")).thenReturn(true);

        assertThatThrownBy(() -> schoolService.createSchool(req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void getAllSchools_shouldReturnAllSchools() {
        when(schoolRepository.findAll()).thenReturn(List.of(testSchool));

        List<SchoolResponse> result = schoolService.getAllSchools();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("school-1");
    }

    @Test
    void getSchool_withValidId_shouldReturnSchoolResponse() {
        when(schoolRepository.findById("school-1")).thenReturn(Optional.of(testSchool));

        SchoolResponse result = schoolService.getSchool("school-1");

        assertThat(result.getName()).isEqualTo("Test School");
    }

    @Test
    void getSchool_withInvalidId_shouldThrowNotFound() {
        when(schoolRepository.findById("bad-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> schoolService.getSchool("bad-id"))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateSchool_withValidData_shouldUpdateFields() {
        UpdateSchoolRequest req = new UpdateSchoolRequest();
        req.setName("Updated School");
        req.setCity("Mumbai");

        when(schoolRepository.findById("school-1")).thenReturn(Optional.of(testSchool));
        when(schoolRepository.save(any())).thenReturn(testSchool);

        schoolService.updateSchool("school-1", req);

        assertThat(testSchool.getName()).isEqualTo("Updated School");
        assertThat(testSchool.getCity()).isEqualTo("Mumbai");
        verify(schoolRepository).save(testSchool);
    }

    @Test
    void deleteSchool_shouldDeactivateInsteadOfDelete() {
        when(schoolRepository.findById("school-1")).thenReturn(Optional.of(testSchool));
        when(schoolRepository.save(any())).thenReturn(testSchool);

        schoolService.deleteSchool("school-1");

        assertThat(testSchool.isActive()).isFalse();
        verify(schoolRepository).save(testSchool);
        verify(schoolRepository, never()).deleteById(any());
    }
}
