package com.school.school_app.service;

import com.school.school_app.dto.request.CreateExamRequest;
import com.school.school_app.dto.request.EnterResultRequest;
import com.school.school_app.dto.response.ExamResponse;
import com.school.school_app.dto.response.ExamResultResponse;
import com.school.school_app.entity.*;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepo;
    private final ExamResultRepository resultRepo;
    private final AcademicYearRepository academicYearRepo;
    private final ClassRoomRepository classRoomRepo;
    private final StudentRepository studentRepo;
    private final SubjectRepository subjectRepo;

    public ExamResponse createExam(String schoolId, String schoolName,
                                   CreateExamRequest req,
                                   String creatorId, String creatorName) {
        AcademicYear ay = academicYearRepo.findById(req.getAcademicYearId())
                .orElseThrow(() -> new AppException("Academic year not found", HttpStatus.NOT_FOUND));
        ClassRoom cr = classRoomRepo.findById(req.getClassRoomId())
                .orElseThrow(() -> new AppException("Class room not found", HttpStatus.NOT_FOUND));

        String status = computeStatus(req.getStartDate(), req.getEndDate());

        Exam exam = Exam.builder()
                .schoolId(schoolId).schoolName(schoolName)
                .academicYearId(ay.getId()).academicYearName(ay.getLabel())
                .classRoomId(cr.getId()).classRoomName(cr.getName())
                .title(req.getTitle()).description(req.getDescription())
                .startDate(req.getStartDate()).endDate(req.getEndDate())
                .status(status)
                .createdById(creatorId).createdByName(creatorName)
                .build();
        return ExamResponse.from(examRepo.save(exam));
    }

    public List<ExamResponse> getExams(String schoolId) {
        return examRepo.findBySchoolId(schoolId).stream().map(ExamResponse::from).toList();
    }

    public ExamResponse getExam(String schoolId, String examId) {
        return ExamResponse.from(examRepo.findByIdAndSchoolId(examId, schoolId)
                .orElseThrow(() -> new AppException("Exam not found", HttpStatus.NOT_FOUND)));
    }

    public ExamResultResponse enterResult(String schoolId, EnterResultRequest req,
                                          String enteredById, String enteredByName) {
        Exam exam = examRepo.findByIdAndSchoolId(req.getExamId(), schoolId)
                .orElseThrow(() -> new AppException("Exam not found", HttpStatus.NOT_FOUND));
        Student student = studentRepo.findById(req.getStudentId())
                .filter(s -> s.getSchoolId().equals(schoolId))
                .orElseThrow(() -> new AppException("Student not found", HttpStatus.NOT_FOUND));
        Subject subject = subjectRepo.findById(req.getSubjectId())
                .orElseThrow(() -> new AppException("Subject not found", HttpStatus.NOT_FOUND));

        if (resultRepo.existsByExamIdAndStudentIdAndSubjectId(req.getExamId(), req.getStudentId(), req.getSubjectId())) {
            throw new AppException("Result already entered for this student and subject", HttpStatus.CONFLICT);
        }

        double pct = req.getMaxMarks() > 0 ? (req.getMarksObtained() / req.getMaxMarks()) * 100 : 0;
        String grade = computeGrade(pct);

        ExamResult result = ExamResult.builder()
                .schoolId(schoolId)
                .examId(exam.getId()).examTitle(exam.getTitle())
                .studentId(student.getId()).studentFullName(student.getFullName())
                .admissionNumber(student.getAdmissionNumber())
                .subjectId(subject.getId()).subjectName(subject.getName())
                .marksObtained(req.getMarksObtained()).maxMarks(req.getMaxMarks())
                .percentage(pct).grade(grade)
                .enteredById(enteredById).enteredByName(enteredByName)
                .build();
        return ExamResultResponse.from(resultRepo.save(result));
    }

    public List<ExamResultResponse> getExamResults(String examId) {
        return resultRepo.findByExamId(examId).stream().map(ExamResultResponse::from).toList();
    }

    public List<ExamResultResponse> getStudentResults(String schoolId, String studentId) {
        return resultRepo.findBySchoolIdAndStudentId(schoolId, studentId)
                .stream().map(ExamResultResponse::from).toList();
    }

    private String computeStatus(LocalDate start, LocalDate end) {
        LocalDate today = LocalDate.now();
        if (today.isBefore(start)) return "UPCOMING";
        if (today.isAfter(end)) return "COMPLETED";
        return "ONGOING";
    }

    private String computeGrade(double pct) {
        if (pct >= 90) return "A+";
        if (pct >= 80) return "A";
        if (pct >= 70) return "B+";
        if (pct >= 60) return "B";
        if (pct >= 50) return "C";
        if (pct >= 40) return "D";
        return "F";
    }
}
