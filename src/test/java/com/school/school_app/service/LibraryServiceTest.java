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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock LibraryBookRepository bookRepo;
    @Mock LibraryIssueRepository issueRepo;

    @InjectMocks LibraryService libraryService;

    private static final String SCHOOL_ID = "school1";

    @Test
    void addBook_success() {
        AddBookRequest req = new AddBookRequest();
        req.setTitle("Java Fundamentals");
        req.setIsbn("978-1234");
        req.setTotalCopies(5);
        req.setAuthor("Author");

        LibraryBook saved = LibraryBook.builder().id("b1").title("Java Fundamentals").availableCopies(5).build();

        when(bookRepo.existsBySchoolIdAndIsbn(SCHOOL_ID, "978-1234")).thenReturn(false);
        when(bookRepo.save(any())).thenReturn(saved);

        LibraryBookResponse result = libraryService.addBook(SCHOOL_ID, "Test", req, "u1", "Admin");

        assertThat(result.getId()).isEqualTo("b1");
        assertThat(result.getAvailableCopies()).isEqualTo(5);
    }

    @Test
    void addBook_duplicateIsbnThrows() {
        AddBookRequest req = new AddBookRequest();
        req.setIsbn("978-1234");

        when(bookRepo.existsBySchoolIdAndIsbn(SCHOOL_ID, "978-1234")).thenReturn(true);

        assertThatThrownBy(() -> libraryService.addBook(SCHOOL_ID, "Test", req, "u1", "Admin"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void getBooks_returnsList() {
        LibraryBook b = LibraryBook.builder().id("b1").schoolId(SCHOOL_ID).build();
        when(bookRepo.findBySchoolId(SCHOOL_ID)).thenReturn(List.of(b));

        List<LibraryBookResponse> result = libraryService.getBooks(SCHOOL_ID);

        assertThat(result).hasSize(1);
    }

    @Test
    void issueBook_success() {
        IssueBookRequest req = new IssueBookRequest();
        req.setBookId("b1");
        req.setMemberId("s1");
        req.setMemberRole("STUDENT");
        req.setDueDate(LocalDate.now().plusDays(14));

        LibraryBook book = LibraryBook.builder().id("b1").schoolId(SCHOOL_ID)
                .title("Math").isbn("111").availableCopies(2).build();
        LibraryIssue saved = LibraryIssue.builder().id("i1").bookId("b1").status("ISSUED").build();

        when(bookRepo.findById("b1")).thenReturn(Optional.of(book));
        when(bookRepo.save(any())).thenReturn(book);
        when(issueRepo.save(any())).thenReturn(saved);

        LibraryIssueResponse result = libraryService.issueBook(SCHOOL_ID, req, "u1", "Admin");

        assertThat(result.getId()).isEqualTo("i1");
        assertThat(result.getStatus()).isEqualTo("ISSUED");
        verify(bookRepo).save(argThat(b -> b.getAvailableCopies() == 1));
    }

    @Test
    void issueBook_noAvailabilityThrows() {
        IssueBookRequest req = new IssueBookRequest();
        req.setBookId("b1");

        LibraryBook book = LibraryBook.builder().id("b1").schoolId(SCHOOL_ID).availableCopies(0).build();
        when(bookRepo.findById("b1")).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> libraryService.issueBook(SCHOOL_ID, req, "u1", "Admin"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("No copies available");
    }

    @Test
    void returnBook_success() {
        LibraryIssue issue = LibraryIssue.builder().id("i1").schoolId(SCHOOL_ID)
                .bookId("b1").status("ISSUED").build();
        LibraryBook book = LibraryBook.builder().id("b1").availableCopies(1).build();

        when(issueRepo.findById("i1")).thenReturn(Optional.of(issue));
        when(bookRepo.findById("b1")).thenReturn(Optional.of(book));
        when(bookRepo.save(any())).thenReturn(book);

        LibraryIssueResponse result = libraryService.returnBook(SCHOOL_ID, "i1");

        assertThat(result.getStatus()).isEqualTo("RETURNED");
    }

    @Test
    void returnBook_alreadyReturnedThrows() {
        LibraryIssue issue = LibraryIssue.builder().id("i1").schoolId(SCHOOL_ID).status("RETURNED").build();
        when(issueRepo.findById("i1")).thenReturn(Optional.of(issue));

        assertThatThrownBy(() -> libraryService.returnBook(SCHOOL_ID, "i1"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("already returned");
    }

    @Test
    void getActiveIssues_returnsList() {
        LibraryIssue i = LibraryIssue.builder().id("i1").status("ISSUED").build();
        when(issueRepo.findBySchoolIdAndStatus(SCHOOL_ID, "ISSUED")).thenReturn(List.of(i));

        List<LibraryIssueResponse> result = libraryService.getActiveIssues(SCHOOL_ID);

        assertThat(result).hasSize(1);
    }
}
