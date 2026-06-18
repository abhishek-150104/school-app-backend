package com.school.school_app.repository;

import com.school.school_app.entity.ClassRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ClassRoomRepository extends MongoRepository<ClassRoom, String> {
    List<ClassRoom> findBySchoolIdOrderByDisplayOrderAscNameAsc(String schoolId);
    List<ClassRoom> findBySchoolIdAndAcademicYearIdOrderByDisplayOrderAscNameAsc(String schoolId, String academicYearId);
    Optional<ClassRoom> findByIdAndSchoolId(String id, String schoolId);
    boolean existsBySchoolIdAndAcademicYearIdAndName(String schoolId, String academicYearId, String name);
}
