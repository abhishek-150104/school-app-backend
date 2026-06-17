package com.school.school_app.repository;

import com.school.school_app.entity.School;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SchoolRepository extends MongoRepository<School, String> {
    List<School> findAllByActiveTrue();
    Optional<School> findByEmail(String email);
    Optional<School> findByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByAffiliationNumber(String affiliationNumber);
}
