package com.school.school_app.repository;

import com.school.school_app.entity.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends MongoRepository<Student, String> {

    Optional<Student> findByIdAndSchoolId(String id, String schoolId);

    List<Student> findBySchoolIdAndActiveTrue(String schoolId);

    List<Student> findBySchoolIdAndClassRoomIdAndActiveTrue(String schoolId, String classRoomId);

    List<Student> findBySchoolIdAndSectionIdAndActiveTrue(String schoolId, String sectionId);

    List<Student> findByParentId(String parentId);

    boolean existsBySchoolIdAndAdmissionNumber(String schoolId, String admissionNumber);

    boolean existsBySchoolIdAndSectionIdAndRollNumber(String schoolId, String sectionId, int rollNumber);

    @Query("{'schoolId': ?0, 'active': true, $or: [{'fullName': {$regex: ?1, $options: 'i'}}, {'admissionNumber': {$regex: ?1, $options: 'i'}}]}")
    List<Student> searchByNameOrAdmission(String schoolId, String query);

    long countBySchoolId(String schoolId);
}
