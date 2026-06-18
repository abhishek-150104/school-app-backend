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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final HomeworkSubmissionRepository submissionRepository;
    private final SectionService sectionService;
    private final StudentRepository studentRepository;

    @Transactional
    public HomeworkResponse create(String schoolId, String sectionId,
                                   CreateHomeworkRequest request,
                                   String teacherId, String teacherName) {
        Section section = sectionService.findById(sectionId);

        Homework hw = Homework.builder()
                .schoolId(schoolId)
                .sectionId(sectionId)
                .sectionName(section.getName())
                .classRoomId(section.getClassRoomId())
                .classRoomName(section.getClassRoomName())
                .title(request.getTitle())
                .description(request.getDescription())
                .subject(request.getSubject())
                .dueDate(request.getDueDate())
                .assignedById(teacherId)
                .assignedByName(teacherName)
                .build();

        hw = homeworkRepository.save(hw);
        return HomeworkResponse.from(hw);
    }

    public List<HomeworkResponse> getBySection(String schoolId, String sectionId,
                                               LocalDate from, LocalDate to) {
        return homeworkRepository
                .findBySchoolIdAndSectionIdAndDueDateBetweenOrderByDueDateAsc(
                        schoolId, sectionId, from, to)
                .stream().map(HomeworkResponse::from).toList();
    }

    public HomeworkResponse getById(String schoolId, String homeworkId) {
        Homework hw = homeworkRepository.findByIdAndSchoolId(homeworkId, schoolId)
                .orElseThrow(() -> new AppException("Homework not found", HttpStatus.NOT_FOUND));
        return HomeworkResponse.from(hw);
    }

    @Transactional
    public HomeworkResponse update(String schoolId, String homeworkId,
                                   UpdateHomeworkRequest request, String requesterId) {
        Homework hw = homeworkRepository.findByIdAndSchoolId(homeworkId, schoolId)
                .orElseThrow(() -> new AppException("Homework not found", HttpStatus.NOT_FOUND));

        if (!hw.getAssignedById().equals(requesterId)) {
            throw new AppException("Only the assigning teacher can update this homework",
                    HttpStatus.FORBIDDEN);
        }

        if (request.getTitle() != null) hw.setTitle(request.getTitle());
        if (request.getDescription() != null) hw.setDescription(request.getDescription());
        if (request.getSubject() != null) hw.setSubject(request.getSubject());
        if (request.getDueDate() != null) hw.setDueDate(request.getDueDate());

        hw = homeworkRepository.save(hw);
        return HomeworkResponse.from(hw);
    }

    @Transactional
    public void delete(String schoolId, String homeworkId, String requesterId) {
        Homework hw = homeworkRepository.findByIdAndSchoolId(homeworkId, schoolId)
                .orElseThrow(() -> new AppException("Homework not found", HttpStatus.NOT_FOUND));

        if (!hw.getAssignedById().equals(requesterId)) {
            throw new AppException("Only the assigning teacher or admin can delete this homework",
                    HttpStatus.FORBIDDEN);
        }

        homeworkRepository.delete(hw);
    }

    @Transactional
    public HomeworkSubmissionResponse markSubmitted(String schoolId, String homeworkId,
                                                    MarkSubmittedRequest request) {
        Homework hw = homeworkRepository.findByIdAndSchoolId(homeworkId, schoolId)
                .orElseThrow(() -> new AppException("Homework not found", HttpStatus.NOT_FOUND));

        HomeworkSubmission submission = submissionRepository
                .findByHomeworkIdAndStudentId(homeworkId, request.getStudentId())
                .orElse(HomeworkSubmission.builder()
                        .homeworkId(homeworkId)
                        .studentId(request.getStudentId())
                        .schoolId(schoolId)
                        .sectionId(hw.getSectionId())
                        .build());

        submission.setStudentFullName(request.getStudentFullName());
        submission.setAdmissionNumber(request.getAdmissionNumber());
        submission.setSubmittedAt(LocalDateTime.now());
        if (request.getRemarks() != null) submission.setRemarks(request.getRemarks());

        submission = submissionRepository.save(submission);
        return HomeworkSubmissionResponse.from(submission);
    }

    public List<HomeworkSubmissionResponse> getSubmissions(String schoolId, String homeworkId) {
        homeworkRepository.findByIdAndSchoolId(homeworkId, schoolId)
                .orElseThrow(() -> new AppException("Homework not found", HttpStatus.NOT_FOUND));
        return submissionRepository.findByHomeworkId(homeworkId)
                .stream().map(HomeworkSubmissionResponse::from).toList();
    }

    public List<HomeworkResponse> getMyChildHomework(String parentId, String studentId,
                                                     LocalDate from, LocalDate to) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException("Student not found", HttpStatus.NOT_FOUND));

        if (!parentId.equals(student.getParentId())) {
            throw new AppException("You are not authorized to view this student's homework",
                    HttpStatus.FORBIDDEN);
        }

        return homeworkRepository
                .findBySchoolIdAndSectionIdAndDueDateBetweenOrderByDueDateAsc(
                        student.getSchoolId(), student.getSectionId(), from, to)
                .stream().map(HomeworkResponse::from).toList();
    }
}
