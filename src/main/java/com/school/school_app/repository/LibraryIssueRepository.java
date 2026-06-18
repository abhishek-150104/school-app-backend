package com.school.school_app.repository;

import com.school.school_app.entity.LibraryIssue;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LibraryIssueRepository extends MongoRepository<LibraryIssue, String> {
    List<LibraryIssue> findBySchoolIdAndMemberId(String schoolId, String memberId);
    List<LibraryIssue> findBySchoolIdAndStatus(String schoolId, String status);
    List<LibraryIssue> findBySchoolIdAndBookId(String schoolId, String bookId);
    long countBySchoolIdAndBookIdAndStatus(String schoolId, String bookId, String status);
}
