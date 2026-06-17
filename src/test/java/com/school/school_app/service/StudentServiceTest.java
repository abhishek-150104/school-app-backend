package com.school.school_app.service;

import com.school.school_app.dto.request.CreateStudentRequest;
import com.school.school_app.dto.request.LinkParentRequest;
import com.school.school_app.dto.request.TransferStudentRequest;
import com.school.school_app.dto.request.UpdateStudentRequest;
import com.school.school_app.dto.response.StudentResponse;
import com.school.school_app.entity.*;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.StudentRepository;
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
class StudentServiceTest {

    @Mock StudentRepository studentRepository;
    @Mock SchoolService schoolService;
    @Mock AcademicYearService academicYearService;
    @Mock ClassRoomService classRoomService;
    @Mock SectionService sectionService;
    @Mock UserRepository userRepository;

    @InjectMocks StudentService studentService;

    private School testSchool;
    private AcademicYear testYear;
    private ClassRoom testClass;
    private Section testSection;
    private Student testStudent;
    private User testParent;

    @BeforeEach
    void setUp() {
        testSchool = School.builder().id("school-1").name("Test School").build();
        testYear = AcademicYear.builder().id("year-1").schoolId("school-1").label("2024-25").build();
        testClass = ClassRoom.builder().id("class-1").schoolId("school-1")
                .academicYearId("year-1").academicYearLabel("2024-25").name("Class 5").build();
        testSection = Section.builder().id("section-1").classRoomId("class-1").name("A").build();

        testStudent = Student.builder()
                .id("student-1")
                .schoolId("school-1")
                .schoolName("Test School")
                .academicYearId("year-1")
                .academicYearLabel("2024-25")
                .classRoomId("class-1")
                .classRoomName("Class 5")
                .sectionId("section-1")
                .sectionName("A")
                .admissionNumber("ADM-001")
                .rollNumber(1)
                .firstName("Rahul")
                .lastName("Sharma")
                .fullName("Rahul Sharma")
                .dateOfBirth(LocalDate.of(2012, 5, 15))
                .gender(Gender.MALE)
                .active(true)
                .build();

        testParent = User.builder()
                .id("parent-1")
                .fullName("Suresh Sharma")
                .phone("9876543210")
                .role(Role.PARENT)
                .build();
    }

    @Test
    void enroll_withValidData_shouldReturnStudentResponse() {
        CreateStudentRequest req = buildCreateRequest();

        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(academicYearService.findByIdAndSchool("year-1", "school-1")).thenReturn(testYear);
        when(classRoomService.findByIdAndSchool("class-1", "school-1")).thenReturn(testClass);
        when(sectionService.findByIdAndClass("section-1", "class-1")).thenReturn(testSection);
        when(studentRepository.existsBySchoolIdAndAdmissionNumber("school-1", "ADM-001")).thenReturn(false);
        when(studentRepository.existsBySchoolIdAndSectionIdAndRollNumber("school-1", "section-1", 1)).thenReturn(false);
        when(studentRepository.save(any())).thenReturn(testStudent);

        StudentResponse result = studentService.enroll("school-1", req);

        assertThat(result.getId()).isEqualTo("student-1");
        assertThat(result.getFullName()).isEqualTo("Rahul Sharma");
        assertThat(result.getAdmissionNumber()).isEqualTo("ADM-001");
    }

    @Test
    void enroll_withDuplicateAdmissionNumber_shouldThrowConflict() {
        CreateStudentRequest req = buildCreateRequest();

        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(academicYearService.findByIdAndSchool(any(), any())).thenReturn(testYear);
        when(classRoomService.findByIdAndSchool(any(), any())).thenReturn(testClass);
        when(sectionService.findByIdAndClass(any(), any())).thenReturn(testSection);
        when(studentRepository.existsBySchoolIdAndAdmissionNumber("school-1", "ADM-001")).thenReturn(true);

        assertThatThrownBy(() -> studentService.enroll("school-1", req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void enroll_withDuplicateRollNumber_shouldThrowConflict() {
        CreateStudentRequest req = buildCreateRequest();

        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(academicYearService.findByIdAndSchool(any(), any())).thenReturn(testYear);
        when(classRoomService.findByIdAndSchool(any(), any())).thenReturn(testClass);
        when(sectionService.findByIdAndClass(any(), any())).thenReturn(testSection);
        when(studentRepository.existsBySchoolIdAndAdmissionNumber(any(), any())).thenReturn(false);
        when(studentRepository.existsBySchoolIdAndSectionIdAndRollNumber("school-1", "section-1", 1)).thenReturn(true);

        assertThatThrownBy(() -> studentService.enroll("school-1", req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void getAllBySchool_withSectionFilter_shouldReturnSectionStudents() {
        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(studentRepository.findBySchoolIdAndSectionIdAndActiveTrue("school-1", "section-1"))
                .thenReturn(List.of(testStudent));

        List<StudentResponse> result = studentService.getAllBySchool("school-1", null, "section-1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSectionId()).isEqualTo("section-1");
    }

    @Test
    void getAllBySchool_withClassFilter_shouldReturnClassStudents() {
        when(schoolService.findById("school-1")).thenReturn(testSchool);
        when(studentRepository.findBySchoolIdAndClassRoomIdAndActiveTrue("school-1", "class-1"))
                .thenReturn(List.of(testStudent));

        List<StudentResponse> result = studentService.getAllBySchool("school-1", "class-1", null);

        assertThat(result).hasSize(1);
    }

    @Test
    void getById_withValidId_shouldReturnStudent() {
        when(studentRepository.findByIdAndSchoolId("student-1", "school-1"))
                .thenReturn(Optional.of(testStudent));

        StudentResponse result = studentService.getById("school-1", "student-1");

        assertThat(result.getId()).isEqualTo("student-1");
    }

    @Test
    void update_shouldUpdateNameAndSetFullName() {
        UpdateStudentRequest req = new UpdateStudentRequest();
        req.setFirstName("Rohit");
        req.setLastName("Verma");

        when(studentRepository.findByIdAndSchoolId("student-1", "school-1"))
                .thenReturn(Optional.of(testStudent));
        when(studentRepository.save(any())).thenReturn(testStudent);

        studentService.update("school-1", "student-1", req);

        assertThat(testStudent.getFirstName()).isEqualTo("Rohit");
        assertThat(testStudent.getLastName()).isEqualTo("Verma");
        assertThat(testStudent.getFullName()).isEqualTo("Rohit Verma");
    }

    @Test
    void deactivate_shouldSetActiveFalse() {
        when(studentRepository.findByIdAndSchoolId("student-1", "school-1"))
                .thenReturn(Optional.of(testStudent));

        studentService.deactivate("school-1", "student-1");

        assertThat(testStudent.isActive()).isFalse();
        verify(studentRepository).save(testStudent);
    }

    @Test
    void linkParent_withValidParent_shouldSetParentFields() {
        LinkParentRequest req = new LinkParentRequest();
        req.setParentId("parent-1");

        when(studentRepository.findByIdAndSchoolId("student-1", "school-1"))
                .thenReturn(Optional.of(testStudent));
        when(userRepository.findById("parent-1")).thenReturn(Optional.of(testParent));
        when(studentRepository.save(any())).thenReturn(testStudent);

        studentService.linkParent("school-1", "student-1", req);

        assertThat(testStudent.getParentId()).isEqualTo("parent-1");
        assertThat(testStudent.getParentName()).isEqualTo("Suresh Sharma");
        assertThat(testStudent.getParentPhone()).isEqualTo("9876543210");
    }

    @Test
    void linkParent_withNonParentUser_shouldThrowBadRequest() {
        LinkParentRequest req = new LinkParentRequest();
        req.setParentId("teacher-1");

        User teacher = User.builder().id("teacher-1").role(Role.TEACHER).build();

        when(studentRepository.findByIdAndSchoolId("student-1", "school-1"))
                .thenReturn(Optional.of(testStudent));
        when(userRepository.findById("teacher-1")).thenReturn(Optional.of(teacher));

        assertThatThrownBy(() -> studentService.linkParent("school-1", "student-1", req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void transfer_shouldUpdateClassSectionAndRollNumber() {
        ClassRoom newClass = ClassRoom.builder().id("class-2").schoolId("school-1")
                .academicYearId("year-1").academicYearLabel("2024-25").name("Class 6").build();
        Section newSection = Section.builder().id("section-2").classRoomId("class-2").name("B").build();

        TransferStudentRequest req = new TransferStudentRequest();
        req.setClassRoomId("class-2");
        req.setSectionId("section-2");
        req.setRollNumber(5);

        when(studentRepository.findByIdAndSchoolId("student-1", "school-1"))
                .thenReturn(Optional.of(testStudent));
        when(classRoomService.findByIdAndSchool("class-2", "school-1")).thenReturn(newClass);
        when(sectionService.findByIdAndClass("section-2", "class-2")).thenReturn(newSection);
        when(studentRepository.existsBySchoolIdAndSectionIdAndRollNumber("school-1", "section-2", 5)).thenReturn(false);
        when(studentRepository.save(any())).thenReturn(testStudent);

        studentService.transfer("school-1", "student-1", req);

        assertThat(testStudent.getClassRoomId()).isEqualTo("class-2");
        assertThat(testStudent.getClassRoomName()).isEqualTo("Class 6");
        assertThat(testStudent.getSectionId()).isEqualTo("section-2");
        assertThat(testStudent.getSectionName()).isEqualTo("B");
        assertThat(testStudent.getRollNumber()).isEqualTo(5);
    }

    @Test
    void getMyChildren_shouldReturnParentStudents() {
        when(studentRepository.findByParentId("parent-1")).thenReturn(List.of(testStudent));

        List<StudentResponse> result = studentService.getMyChildren("parent-1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("student-1");
    }

    private CreateStudentRequest buildCreateRequest() {
        CreateStudentRequest req = new CreateStudentRequest();
        req.setFirstName("Rahul");
        req.setLastName("Sharma");
        req.setDateOfBirth(LocalDate.of(2012, 5, 15));
        req.setGender(Gender.MALE);
        req.setAdmissionNumber("ADM-001");
        req.setRollNumber(1);
        req.setAdmissionDate(LocalDate.now());
        req.setAcademicYearId("year-1");
        req.setClassRoomId("class-1");
        req.setSectionId("section-1");
        return req;
    }
}
