package com.school.school_app.repository;

import com.school.school_app.entity.Circular;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface CircularRepository extends MongoRepository<Circular, String> {
    List<Circular> findBySchoolIdOrderByPublishedAtDesc(String schoolId);
    Optional<Circular> findByIdAndSchoolId(String id, String schoolId);
}
