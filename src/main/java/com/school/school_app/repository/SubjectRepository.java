package com.school.school_app.repository;

import com.school.school_app.entity.Subject;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends MongoRepository<Subject, String> {

    List<Subject> findByClassRoomIdOrderByNameAsc(String classRoomId);

    boolean existsByClassRoomIdAndName(String classRoomId, String name);

    Optional<Subject> findByIdAndSchoolId(String id, String schoolId);

    void deleteByClassRoomId(String classRoomId);
}
