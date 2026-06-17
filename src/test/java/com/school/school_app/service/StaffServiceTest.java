package com.school.school_app.service;

import com.school.school_app.dto.request.CreateStaffRequest;
import com.school.school_app.dto.request.UpdateStaffRequest;
import com.school.school_app.dto.response.StaffResponse;
import com.school.school_app.dto.response.TeacherProfileResponse;
import com.school.school_app.entity.*;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.SectionRepository;
import com.school.school_app.repository.StaffRepository;
import com.school.school_app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffServiceTest {

    @Mock StaffRepository staffRepository;
    @Mock SchoolService schoolService;
    @Mock UserRepository userRepository;
    @Mock SectionRepository sectionRepository;

    @InjectMocks StaffService staffService;

    private School testSchool;
    private User testTeacher;
    private Staff testStaff;

    @BeforeEach
    void setUp() {
        testSchool = School.builder().id("school-1").name("Test School").build();

        testTeacher = User.builder()
                .id("user-1")
                .fullName("Amit Sharma")
                .email("amit@school.com")
                .role(Role.TEACHER)
                .build();

        testStaff = Staff.builder()
                .id("staff-1")
                .schoolId("school-1")
                .schoolName("Test School")
                .userId("user-1")
                .employeeId("EMP-001")
                .firstName("Amit")
                .lastName("Sharma")
                .fullName("Amit Sharma")
                .designation("Class Teacher")
                .subjects(List.of("Mathematics", "Science"))
                .joiningDate(LocalDate.of(2022, 6, 1))
                .active(true)
                .build();
    }

    @Test
    void create_withValidData_shouldReturnStaffResponse() {
        CreateStaffRequest req = buildCreateRequest();

        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(staffRepository.existsBySchoolIdAndEmployeeId("school-1", "EMP-001")).thenReturn(false);
        when(staffRepository.existsByUserId("user-1")).thenReturn(false);
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testTeacher));
        when(staffRepository.save(any())).thenReturn(testStaff);

        StaffResponse result = staffService.create("school-1", req);

        assertThat(result.getId()).isEqualTo("staff-1");
        assertThat(result.getFullName()).isEqualTo("Amit Sharma");
        assertThat(result.getEmployeeId()).isEqualTo("EMP-001");
    }

    @Test
    void create_withDuplicateEmployeeId_shouldThrowConflict() {
        CreateStaffRequest req = buildCreateRequest();

        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(staffRepository.existsBySchoolIdAndEmployeeId("school-1", "EMP-001")).thenReturn(true);

        assertThatThrownBy(() -> staffService.create("school-1", req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void create_withAlreadyLinkedUser_shouldThrowConflict() {
        CreateStaffRequest req = buildCreateRequest();

        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(staffRepository.existsBySchoolIdAndEmployeeId(any(), any())).thenReturn(false);
        when(staffRepository.existsByUserId("user-1")).thenReturn(true);

        assertThatThrownBy(() -> staffService.create("school-1", req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void create_withNonTeacherUser_shouldThrowBadRequest() {
        CreateStaffRequest req = buildCreateRequest();
        User admin = User.builder().id("user-1").role(Role.SCHOOL_ADMIN).build();

        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(staffRepository.existsBySchoolIdAndEmployeeId(any(), any())).thenReturn(false);
        when(staffRepository.existsByUserId(any())).thenReturn(false);
        when(userRepository.findById("user-1")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> staffService.create("school-1", req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getAllBySchool_shouldReturnActiveStaff() {
        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(staffRepository.findBySchoolIdAndActiveTrue("school-1")).thenReturn(List.of(testStaff));

        List<StaffResponse> result = staffService.getAllBySchool("school-1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmployeeId()).isEqualTo("EMP-001");
    }

    @Test
    void search_withQuery_shouldReturnMatchingStaff() {
        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(staffRepository.searchByNameOrEmployeeId("school-1", "Amit"))
                .thenReturn(List.of(testStaff));

        List<StaffResponse> result = staffService.search("school-1", "Amit");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullName()).isEqualTo("Amit Sharma");
    }

    @Test
    void getById_withValidId_shouldReturnStaff() {
        when(staffRepository.findByIdAndSchoolId("staff-1", "school-1"))
                .thenReturn(Optional.of(testStaff));

        StaffResponse result = staffService.getById("school-1", "staff-1");

        assertThat(result.getId()).isEqualTo("staff-1");
    }

    @Test
    void update_shouldUpdateDesignationAndSubjects() {
        UpdateStaffRequest req = new UpdateStaffRequest();
        req.setDesignation("HOD Mathematics");
        req.setSubjects(List.of("Mathematics"));

        when(staffRepository.findByIdAndSchoolId("staff-1", "school-1"))
                .thenReturn(Optional.of(testStaff));
        when(staffRepository.save(any())).thenReturn(testStaff);

        staffService.update("school-1", "staff-1", req);

        assertThat(testStaff.getDesignation()).isEqualTo("HOD Mathematics");
        assertThat(testStaff.getSubjects()).containsExactly("Mathematics");
    }

    @Test
    void deactivate_shouldSetActiveFalse() {
        when(staffRepository.findByIdAndSchoolId("staff-1", "school-1"))
                .thenReturn(Optional.of(testStaff));

        staffService.deactivate("school-1", "staff-1");

        assertThat(testStaff.isActive()).isFalse();
        verify(staffRepository).save(testStaff);
    }

    @Test
    void getMyProfile_shouldReturnProfileWithAssignedSections() {
        Section section = Section.builder()
                .id("section-1")
                .classRoomId("class-1")
                .classRoomName("Class 5")
                .name("A")
                .capacity(40)
                .classTeacherId("user-1")
                .classTeacherName("Amit Sharma")
                .build();

        when(staffRepository.findByUserId("user-1")).thenReturn(Optional.of(testStaff));
        when(sectionRepository.findByClassTeacherId("user-1")).thenReturn(List.of(section));

        TeacherProfileResponse result = staffService.getMyProfile("user-1");

        assertThat(result.getProfile().getId()).isEqualTo("staff-1");
        assertThat(result.getAssignedSections()).hasSize(1);
        assertThat(result.getAssignedSections().get(0).getId()).isEqualTo("section-1");
    }

    @Test
    void getMyProfile_withNoProfile_shouldThrowNotFound() {
        when(staffRepository.findByUserId("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> staffService.getMyProfile("unknown"))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    private CreateStaffRequest buildCreateRequest() {
        CreateStaffRequest req = new CreateStaffRequest();
        req.setUserId("user-1");
        req.setEmployeeId("EMP-001");
        req.setFirstName("Amit");
        req.setLastName("Sharma");
        req.setDesignation("Class Teacher");
        req.setSubjects(List.of("Mathematics", "Science"));
        req.setJoiningDate(LocalDate.of(2022, 6, 1));
        return req;
    }
}
