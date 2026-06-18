package com.school.school_app.service;

import com.school.school_app.dto.request.AddBookRequest;
import com.school.school_app.dto.request.IssueBookRequest;
import com.school.school_app.dto.response.LibraryBookResponse;
import com.school.school_app.dto.response.LibraryIssueResponse;
import com.school.school_app.entity.LibraryBook;
import com.school.school_app.entity.LibraryIssue;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.LibraryBookRepository;
import com.school.school_app.repository.LibraryIssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final LibraryBookRepository bookRepo;
    private final LibraryIssueRepository issueRepo;

    public LibraryBookResponse addBook(String schoolId, String schoolName,
                                       AddBookRequest req,
                                       String addedById, String addedByName) {
        if (bookRepo.existsBySchoolIdAndIsbn(schoolId, req.getIsbn())) {
            throw new AppException("Book with this ISBN already exists", HttpStatus.CONFLICT);
        }
        LibraryBook book = LibraryBook.builder()
                .schoolId(schoolId).schoolName(schoolName)
                .title(req.getTitle()).author(req.getAuthor()).isbn(req.getIsbn())
                .category(req.getCategory()).publisher(req.getPublisher())
                .publishYear(req.getPublishYear())
                .totalCopies(req.getTotalCopies()).availableCopies(req.getTotalCopies())
                .addedById(addedById).addedByName(addedByName)
                .build();
        return LibraryBookResponse.from(bookRepo.save(book));
    }

    public List<LibraryBookResponse> getBooks(String schoolId) {
        return bookRepo.findBySchoolId(schoolId).stream().map(LibraryBookResponse::from).toList();
    }

    public LibraryIssueResponse issueBook(String schoolId, IssueBookRequest req,
                                          String issuedById, String issuedByName) {
        LibraryBook book = bookRepo.findById(req.getBookId())
                .filter(b -> b.getSchoolId().equals(schoolId))
                .orElseThrow(() -> new AppException("Book not found", HttpStatus.NOT_FOUND));

        if (book.getAvailableCopies() <= 0) {
            throw new AppException("No copies available", HttpStatus.BAD_REQUEST);
        }

        LibraryIssue issue = LibraryIssue.builder()
                .schoolId(schoolId)
                .bookId(book.getId()).bookTitle(book.getTitle()).bookIsbn(book.getIsbn())
                .memberId(req.getMemberId()).memberRole(req.getMemberRole())
                .issuedDate(LocalDate.now()).dueDate(req.getDueDate())
                .status("ISSUED")
                .issuedById(issuedById).issuedByName(issuedByName)
                .build();

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepo.save(book);
        return LibraryIssueResponse.from(issueRepo.save(issue));
    }

    public LibraryIssueResponse returnBook(String schoolId, String issueId) {
        LibraryIssue issue = issueRepo.findById(issueId)
                .filter(i -> i.getSchoolId().equals(schoolId))
                .orElseThrow(() -> new AppException("Issue record not found", HttpStatus.NOT_FOUND));

        if ("RETURNED".equals(issue.getStatus())) {
            throw new AppException("Book already returned", HttpStatus.BAD_REQUEST);
        }

        issue.setReturnedDate(LocalDate.now());
        issue.setStatus("RETURNED");
        issueRepo.save(issue);

        bookRepo.findById(issue.getBookId()).ifPresent(b -> {
            b.setAvailableCopies(b.getAvailableCopies() + 1);
            bookRepo.save(b);
        });

        return LibraryIssueResponse.from(issue);
    }

    public List<LibraryIssueResponse> getMemberIssues(String schoolId, String memberId) {
        return issueRepo.findBySchoolIdAndMemberId(schoolId, memberId)
                .stream().map(LibraryIssueResponse::from).toList();
    }

    public List<LibraryIssueResponse> getActiveIssues(String schoolId) {
        return issueRepo.findBySchoolIdAndStatus(schoolId, "ISSUED")
                .stream().map(LibraryIssueResponse::from).toList();
    }
}
