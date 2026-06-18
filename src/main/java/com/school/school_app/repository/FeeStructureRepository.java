package com.school.school_app.repository;

import com.school.school_app.entity.FeeStructure;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FeeStructureRepository extends MongoRepository<FeeStructure, String> {
    List<FeeStructure> findBySchoolId(String schoolId);
    List<FeeStructure> findBySchoolIdAndAcademicYearId(String schoolId, String academicYearId);
    Optional<FeeStructure> findBySchoolIdAndAcademicYearIdAndClassRoomId(String schoolId, String academicYearId, String classRoomId);
    boolean existsBySchoolIdAndAcademicYearIdAndClassRoomId(String schoolId, String academicYearId, String classRoomId);
}
