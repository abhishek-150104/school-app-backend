package com.school.school_app.dto.response;

import com.school.school_app.entity.LibraryIssue;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class LibraryIssueResponse {
    private String id;
    private String bookId;
    private String bookTitle;
    private String bookIsbn;
    private String memberId;
    private String memberName;
    private String memberRole;
    private LocalDate issuedDate;
    private LocalDate dueDate;
    private LocalDate returnedDate;
    private String status;

    public static LibraryIssueResponse from(LibraryIssue i) {
        return LibraryIssueResponse.builder()
                .id(i.getId()).bookId(i.getBookId()).bookTitle(i.getBookTitle())
                .bookIsbn(i.getBookIsbn()).memberId(i.getMemberId())
                .memberName(i.getMemberName()).memberRole(i.getMemberRole())
                .issuedDate(i.getIssuedDate()).dueDate(i.getDueDate())
                .returnedDate(i.getReturnedDate()).status(i.getStatus())
                .build();
    }
}
