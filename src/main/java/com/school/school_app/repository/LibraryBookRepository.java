package com.school.school_app.repository;

import com.school.school_app.entity.LibraryBook;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface LibraryBookRepository extends MongoRepository<LibraryBook, String> {
    List<LibraryBook> findBySchoolId(String schoolId);
    List<LibraryBook> findBySchoolIdAndCategory(String schoolId, String category);
    Optional<LibraryBook> findBySchoolIdAndIsbn(String schoolId, String isbn);
    boolean existsBySchoolIdAndIsbn(String schoolId, String isbn);
}
