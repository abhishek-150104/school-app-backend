package com.school.school_app.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "library_issues")
@CompoundIndexes({
        @CompoundIndex(def = "{'schoolId': 1, 'bookId': 1, 'memberId': 1, 'returnedAt': 1}"),
        @CompoundIndex(def = "{'schoolId': 1, 'memberId': 1, 'status': 1}")
})
public class LibraryIssue {

    @Id
    private String id;

    private String schoolId;

    private String bookId;
    private String bookTitle;
    private String bookIsbn;

    private String memberId;
    private String memberName;
    private String memberRole;

    private LocalDate issuedDate;
    private LocalDate dueDate;
    private LocalDate returnedDate;

    private String status; // ISSUED, RETURNED, OVERDUE

    private String issuedById;
    private String issuedByName;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
