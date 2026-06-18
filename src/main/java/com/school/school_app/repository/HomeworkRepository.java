package com.school.school_app.repository;

import com.school.school_app.entity.Homework;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HomeworkRepository extends MongoRepository<Homework, String> {

    List<Homework> findBySchoolIdAndSectionIdAndDueDateBetweenOrderByDueDateAsc(
            String schoolId, String sectionId, LocalDate from, LocalDate to);

    Optional<Homework> findByIdAndSchoolId(String id, String schoolId);
}
