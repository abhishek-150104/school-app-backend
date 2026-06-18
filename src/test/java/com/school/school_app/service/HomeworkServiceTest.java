package com.school.school_app.service;

import com.school.school_app.dto.request.CreateHomeworkRequest;
import com.school.school_app.dto.request.MarkSubmittedRequest;
import com.school.school_app.dto.request.UpdateHomeworkRequest;
import com.school.school_app.dto.response.HomeworkResponse;
import com.school.school_app.dto.response.HomeworkSubmissionResponse;
import com.school.school_app.entity.Homework;
import com.school.school_app.entity.HomeworkSubmission;
import com.school.school_app.entity.Section;
import com.school.school_app.entity.Student;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.HomeworkRepository;
import com.school.school_app.repository.HomeworkSubmissionRepository;
import com.school.school_app.repository.StudentRepository;
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
class HomeworkServiceTest {

    @Mock HomeworkRepository homeworkRepository;
    @Mock HomeworkSubmissionRepository submissionRepository;
    @Mock SectionService sectionService;
    @Mock StudentRepository studentRepository;

    @InjectMocks HomeworkService homeworkService;

    private static final String SCHOOL_ID = "school1";
    private static final String SECTION_ID = "section1";
    private static final String HW_ID = "hw1";
    private static final String TEACHER_ID = "teacher1";
    private static final String STUDENT_ID = "student1";

    private Section section;
    private Homework homework;

    @BeforeEach
    void setUp() {
        section = new Section();
        section.setId(SECTION_ID);
        section.setClassRoomId("class1");
        section.setClassRoomName("Class 5");
        section.setName("Section A");

        homework = Homework.builder()
                .id(HW_ID)
                .schoolId(SCHOOL_ID)
                .sectionId(SECTION_ID)
                .sectionName("Section A")
                .classRoomId("class1")
                .classRoomName("Class 5")
                .title("Math Homework")
                .description("Solve exercises 1-10")
                .subject("Mathematics")
                .dueDate(LocalDate.now().plusDays(3))
                .assignedById(TEACHER_ID)
                .assignedByName("Teacher One")
                .build();
    }

    @Test
    void create_savesHomework_withSectionSnapshot() {
        CreateHomeworkRequest request = new CreateHomeworkRequest();
        request.setSectionId(SECTION_ID);
        request.setTitle("Math Homework");
        request.setDescription("Solve exercises 1-10");
        request.setSubject("Mathematics");
        request.setDueDate(LocalDate.now().plusDays(3));

        when(sectionService.findById(SECTION_ID)).thenReturn(section);
        when(homeworkRepository.save(any(Homework.class))).thenReturn(homework);

        HomeworkResponse response = homeworkService.create(SCHOOL_ID, SECTION_ID, request, TEACHER_ID, "Teacher One");

        assertThat(response.getTitle()).isEqualTo("Math Homework");
        assertThat(response.getSectionName()).isEqualTo("Section A");
        assertThat(response.getClassRoomName()).isEqualTo("Class 5");
        verify(homeworkRepository).save(any(Homework.class));
    }

    @Test
    void getBySection_returnsFilteredList() {
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.now().plusDays(7);

        when(homeworkRepository.findBySchoolIdAndSectionIdAndDueDateBetweenOrderByDueDateAsc(
                SCHOOL_ID, SECTION_ID, from, to)).thenReturn(List.of(homework));

        List<HomeworkResponse> result = homeworkService.getBySection(SCHOOL_ID, SECTION_ID, from, to);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Math Homework");
    }

    @Test
    void getById_returnsHomework_whenFound() {
        when(homeworkRepository.findByIdAndSchoolId(HW_ID, SCHOOL_ID)).thenReturn(Optional.of(homework));

        HomeworkResponse response = homeworkService.getById(SCHOOL_ID, HW_ID);

        assertThat(response.getId()).isEqualTo(HW_ID);
        assertThat(response.getSubject()).isEqualTo("Mathematics");
    }

    @Test
    void getById_throwsNotFound_whenMissing() {
        when(homeworkRepository.findByIdAndSchoolId("bad", SCHOOL_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> homeworkService.getById(SCHOOL_ID, "bad"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("not found")
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void update_updatesTitle_whenRequesterIsOwner() {
        UpdateHomeworkRequest request = new UpdateHomeworkRequest();
        request.setTitle("Updated Title");

        when(homeworkRepository.findByIdAndSchoolId(HW_ID, SCHOOL_ID)).thenReturn(Optional.of(homework));
        when(homeworkRepository.save(any(Homework.class))).thenAnswer(inv -> inv.getArgument(0));

        HomeworkResponse response = homeworkService.update(SCHOOL_ID, HW_ID, request, TEACHER_ID);

        assertThat(response.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void update_throwsForbidden_whenRequesterIsNotOwner() {
        UpdateHomeworkRequest request = new UpdateHomeworkRequest();
        request.setTitle("Hacked Title");

        when(homeworkRepository.findByIdAndSchoolId(HW_ID, SCHOOL_ID)).thenReturn(Optional.of(homework));

        assertThatThrownBy(() -> homeworkService.update(SCHOOL_ID, HW_ID, request, "other-user"))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void delete_throwsForbidden_whenRequesterIsNotOwner() {
        when(homeworkRepository.findByIdAndSchoolId(HW_ID, SCHOOL_ID)).thenReturn(Optional.of(homework));

        assertThatThrownBy(() -> homeworkService.delete(SCHOOL_ID, HW_ID, "intruder"))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void markSubmitted_createsNewSubmission_whenNotExists() {
        MarkSubmittedRequest request = new MarkSubmittedRequest();
        request.setStudentId(STUDENT_ID);
        request.setAdmissionNumber("ADM001");
        request.setStudentFullName("John Doe");
        request.setRemarks("On time");

        HomeworkSubmission saved = HomeworkSubmission.builder()
                .id("sub1")
                .homeworkId(HW_ID)
                .studentId(STUDENT_ID)
                .studentFullName("John Doe")
                .admissionNumber("ADM001")
                .schoolId(SCHOOL_ID)
                .sectionId(SECTION_ID)
                .remarks("On time")
                .build();

        when(homeworkRepository.findByIdAndSchoolId(HW_ID, SCHOOL_ID)).thenReturn(Optional.of(homework));
        when(submissionRepository.findByHomeworkIdAndStudentId(HW_ID, STUDENT_ID)).thenReturn(Optional.empty());
        when(submissionRepository.save(any(HomeworkSubmission.class))).thenReturn(saved);

        HomeworkSubmissionResponse response = homeworkService.markSubmitted(SCHOOL_ID, HW_ID, request);

        assertThat(response.getStudentFullName()).isEqualTo("John Doe");
        assertThat(response.getRemarks()).isEqualTo("On time");
        verify(submissionRepository).save(any(HomeworkSubmission.class));
    }

    @Test
    void getSubmissions_returnsListForHomework() {
        HomeworkSubmission sub = HomeworkSubmission.builder()
                .id("sub1").homeworkId(HW_ID).studentId(STUDENT_ID)
                .studentFullName("Jane Doe").admissionNumber("ADM002")
                .build();

        when(homeworkRepository.findByIdAndSchoolId(HW_ID, SCHOOL_ID)).thenReturn(Optional.of(homework));
        when(submissionRepository.findByHomeworkId(HW_ID)).thenReturn(List.of(sub));

        List<HomeworkSubmissionResponse> result = homeworkService.getSubmissions(SCHOOL_ID, HW_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStudentFullName()).isEqualTo("Jane Doe");
    }

    @Test
    void getMyChildHomework_throwsForbidden_whenParentDoesNotOwnStudent() {
        Student student = Student.builder()
                .id(STUDENT_ID)
                .schoolId(SCHOOL_ID)
                .sectionId(SECTION_ID)
                .parentId("other-parent")
                .build();

        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));

        assertThatThrownBy(() -> homeworkService.getMyChildHomework(
                "my-parent-id", STUDENT_ID, LocalDate.now(), LocalDate.now().plusDays(7)))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void getMyChildHomework_returnsHomework_whenParentOwnsStudent() {
        Student student = Student.builder()
                .id(STUDENT_ID)
                .schoolId(SCHOOL_ID)
                .sectionId(SECTION_ID)
                .parentId("parent1")
                .build();

        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.now().plusDays(7);

        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        when(homeworkRepository.findBySchoolIdAndSectionIdAndDueDateBetweenOrderByDueDateAsc(
                SCHOOL_ID, SECTION_ID, from, to)).thenReturn(List.of(homework));

        List<HomeworkResponse> result = homeworkService.getMyChildHomework("parent1", STUDENT_ID, from, to);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Math Homework");
    }
}
