package com.school.school_app.repository;

import com.school.school_app.entity.AcademicYear;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AcademicYearRepository extends MongoRepository<AcademicYear, String> {
    List<AcademicYear> findBySchoolIdOrderByStartYearDesc(String schoolId);
    List<AcademicYear> findBySchoolId(String schoolId);
    Optional<AcademicYear> findBySchoolIdAndActiveTrue(String schoolId);
    boolean existsBySchoolIdAndLabel(String schoolId, String label);
}
