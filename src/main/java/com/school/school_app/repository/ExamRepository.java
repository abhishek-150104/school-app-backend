package com.school.school_app.repository;

import com.school.school_app.entity.Exam;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ExamRepository extends MongoRepository<Exam, String> {
    List<Exam> findBySchoolId(String schoolId);
    List<Exam> findBySchoolIdAndClassRoomId(String schoolId, String classRoomId);
    List<Exam> findBySchoolIdAndAcademicYearId(String schoolId, String academicYearId);
    Optional<Exam> findByIdAndSchoolId(String id, String schoolId);

    long countBySchoolId(String schoolId);
    long countBySchoolIdAndStatus(String schoolId, String status);
}
