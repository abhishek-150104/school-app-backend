package com.school.school_app.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "library_books")
@CompoundIndexes({
        @CompoundIndex(def = "{'schoolId': 1, 'isbn': 1}", unique = true),
        @CompoundIndex(def = "{'schoolId': 1, 'category': 1}")
})
public class LibraryBook {

    @Id
    private String id;

    private String schoolId;
    private String schoolName;

    private String title;
    private String author;
    private String isbn;
    private String category;
    private String publisher;
    private int publishYear;
    private int totalCopies;
    private int availableCopies;

    private String addedById;
    private String addedByName;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
