package com.school.school_app.repository;

import com.school.school_app.entity.HomeworkSubmission;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface HomeworkSubmissionRepository extends MongoRepository<HomeworkSubmission, String> {

    Optional<HomeworkSubmission> findByHomeworkIdAndStudentId(String homeworkId, String studentId);

    List<HomeworkSubmission> findByHomeworkId(String homeworkId);
}
