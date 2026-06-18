package com.school.school_app.repository;

import com.school.school_app.entity.ExamResult;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExamResultRepository extends MongoRepository<ExamResult, String> {
    List<ExamResult> findByExamId(String examId);
    List<ExamResult> findBySchoolIdAndStudentId(String schoolId, String studentId);
    List<ExamResult> findByExamIdAndStudentId(String examId, String studentId);
    boolean existsByExamIdAndStudentIdAndSubjectId(String examId, String studentId, String subjectId);
}
