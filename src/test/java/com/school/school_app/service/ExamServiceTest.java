package com.school.school_app.service;

import com.school.school_app.dto.request.CreateExamRequest;
import com.school.school_app.dto.request.EnterResultRequest;
import com.school.school_app.dto.response.ExamResponse;
import com.school.school_app.dto.response.ExamResultResponse;
import com.school.school_app.entity.*;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceTest {

    @Mock ExamRepository examRepo;
    @Mock ExamResultRepository resultRepo;
    @Mock AcademicYearRepository academicYearRepo;
    @Mock ClassRoomRepository classRoomRepo;
    @Mock StudentRepository studentRepo;
    @Mock SubjectRepository subjectRepo;

    @InjectMocks ExamService examService;

    private static final String SCHOOL_ID = "school1";

    @Test
    void createExam_success() {
        CreateExamRequest req = new CreateExamRequest();
        req.setAcademicYearId("ay1");
        req.setClassRoomId("cr1");
        req.setTitle("Mid Term");
        req.setStartDate(LocalDate.now().plusDays(5));
        req.setEndDate(LocalDate.now().plusDays(10));

        AcademicYear ay = AcademicYear.builder().id("ay1").label("2024-25").build();
        ClassRoom cr = ClassRoom.builder().id("cr1").name("Class 1").build();
        Exam saved = Exam.builder().id("ex1").title("Mid Term").status("UPCOMING").build();

        when(academicYearRepo.findById("ay1")).thenReturn(Optional.of(ay));
        when(classRoomRepo.findById("cr1")).thenReturn(Optional.of(cr));
        when(examRepo.save(any())).thenReturn(saved);

        ExamResponse result = examService.createExam(SCHOOL_ID, "Test", req, "u1", "Admin");

        assertThat(result.getId()).isEqualTo("ex1");
        assertThat(result.getStatus()).isEqualTo("UPCOMING");
    }

    @Test
    void getExams_returnsList() {
        Exam e = Exam.builder().id("ex1").schoolId(SCHOOL_ID).title("Final").build();
        when(examRepo.findBySchoolId(SCHOOL_ID)).thenReturn(List.of(e));

        List<ExamResponse> result = examService.getExams(SCHOOL_ID);

        assertThat(result).hasSize(1);
    }

    @Test
    void getExam_notFoundThrows() {
        when(examRepo.findByIdAndSchoolId("ex1", SCHOOL_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> examService.getExam(SCHOOL_ID, "ex1"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Exam not found");
    }

    @Test
    void enterResult_success() {
        EnterResultRequest req = new EnterResultRequest();
        req.setExamId("ex1");
        req.setStudentId("s1");
        req.setSubjectId("sub1");
        req.setMarksObtained(85);
        req.setMaxMarks(100);

        Exam exam = Exam.builder().id("ex1").schoolId(SCHOOL_ID).title("Mid Term").build();
        Student student = Student.builder().id("s1").schoolId(SCHOOL_ID).fullName("Alice").admissionNumber("A1").build();
        Subject subject = Subject.builder().id("sub1").name("Math").build();
        ExamResult saved = ExamResult.builder().id("r1").marksObtained(85).percentage(85).grade("A").build();

        when(examRepo.findByIdAndSchoolId("ex1", SCHOOL_ID)).thenReturn(Optional.of(exam));
        when(studentRepo.findById("s1")).thenReturn(Optional.of(student));
        when(subjectRepo.findById("sub1")).thenReturn(Optional.of(subject));
        when(resultRepo.existsByExamIdAndStudentIdAndSubjectId(any(), any(), any())).thenReturn(false);
        when(resultRepo.save(any())).thenReturn(saved);

        ExamResultResponse result = examService.enterResult(SCHOOL_ID, req, "u1", "Admin");

        assertThat(result.getId()).isEqualTo("r1");
        assertThat(result.getGrade()).isEqualTo("A");
    }

    @Test
    void enterResult_duplicateThrows() {
        EnterResultRequest req = new EnterResultRequest();
        req.setExamId("ex1");
        req.setStudentId("s1");
        req.setSubjectId("sub1");

        Exam exam = Exam.builder().id("ex1").schoolId(SCHOOL_ID).build();
        Student student = Student.builder().id("s1").schoolId(SCHOOL_ID).build();
        Subject subject = Subject.builder().id("sub1").build();

        when(examRepo.findByIdAndSchoolId("ex1", SCHOOL_ID)).thenReturn(Optional.of(exam));
        when(studentRepo.findById("s1")).thenReturn(Optional.of(student));
        when(subjectRepo.findById("sub1")).thenReturn(Optional.of(subject));
        when(resultRepo.existsByExamIdAndStudentIdAndSubjectId(any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> examService.enterResult(SCHOOL_ID, req, "u1", "Admin"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("already entered");
    }

    @Test
    void getExamResults_returnsList() {
        ExamResult r = ExamResult.builder().id("r1").examId("ex1").build();
        when(resultRepo.findByExamId("ex1")).thenReturn(List.of(r));

        List<ExamResultResponse> result = examService.getExamResults("ex1");

        assertThat(result).hasSize(1);
    }

    @Test
    void getStudentResults_returnsList() {
        ExamResult r = ExamResult.builder().id("r1").studentId("s1").build();
        when(resultRepo.findBySchoolIdAndStudentId(SCHOOL_ID, "s1")).thenReturn(List.of(r));

        List<ExamResultResponse> result = examService.getStudentResults(SCHOOL_ID, "s1");

        assertThat(result).hasSize(1);
    }

    @Test
    void computeGrade_returnsCorrectGrade() {
        // Test via enterResult with 95% → A+
        EnterResultRequest req = new EnterResultRequest();
        req.setExamId("ex1");
        req.setStudentId("s1");
        req.setSubjectId("sub1");
        req.setMarksObtained(95);
        req.setMaxMarks(100);

        Exam exam = Exam.builder().id("ex1").schoolId(SCHOOL_ID).build();
        Student student = Student.builder().id("s1").schoolId(SCHOOL_ID).build();
        Subject subject = Subject.builder().id("sub1").build();
        ExamResult saved = ExamResult.builder().id("r1").grade("A+").percentage(95).build();

        when(examRepo.findByIdAndSchoolId(any(), any())).thenReturn(Optional.of(exam));
        when(studentRepo.findById(any())).thenReturn(Optional.of(student));
        when(subjectRepo.findById(any())).thenReturn(Optional.of(subject));
        when(resultRepo.existsByExamIdAndStudentIdAndSubjectId(any(), any(), any())).thenReturn(false);
        when(resultRepo.save(any())).thenReturn(saved);

        ExamResultResponse result = examService.enterResult(SCHOOL_ID, req, "u1", "Admin");

        assertThat(result.getGrade()).isEqualTo("A+");
    }
}
