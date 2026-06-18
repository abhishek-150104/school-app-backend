package com.school.school_app.controller;

import com.school.school_app.dto.request.AddBookRequest;
import com.school.school_app.dto.request.IssueBookRequest;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.dto.response.LibraryBookResponse;
import com.school.school_app.dto.response.LibraryIssueResponse;
import com.school.school_app.entity.User;
import com.school.school_app.service.LibraryService;
import com.school.school_app.service.SchoolContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;
    private final SchoolContextService schoolContextService;

    @PostMapping("/api/library/books")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<LibraryBookResponse>> addBook(
            @RequestBody AddBookRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Book added",
                libraryService.addBook(schoolContextService.getSchoolId(),
                        schoolContextService.getSchoolName(), request,
                        currentUser.getId(), currentUser.getFullName())));
    }

    @GetMapping("/api/library/books")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<LibraryBookResponse>>> getBooks() {
        return ResponseEntity.ok(ApiResponse.success(
                libraryService.getBooks(schoolContextService.getSchoolId())));
    }

    @PostMapping("/api/library/issues")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<LibraryIssueResponse>> issueBook(
            @RequestBody IssueBookRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Book issued",
                libraryService.issueBook(schoolContextService.getSchoolId(), request,
                        currentUser.getId(), currentUser.getFullName())));
    }

    @PostMapping("/api/library/issues/{issueId}/return")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<LibraryIssueResponse>> returnBook(@PathVariable String issueId) {
        return ResponseEntity.ok(ApiResponse.success("Book returned",
                libraryService.returnBook(schoolContextService.getSchoolId(), issueId)));
    }

    @GetMapping("/api/library/issues/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<List<LibraryIssueResponse>>> getActiveIssues() {
        return ResponseEntity.ok(ApiResponse.success(
                libraryService.getActiveIssues(schoolContextService.getSchoolId())));
    }

    @GetMapping("/api/library/issues/member/{memberId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<LibraryIssueResponse>>> getMemberIssues(
            @PathVariable String memberId) {
        return ResponseEntity.ok(ApiResponse.success(
                libraryService.getMemberIssues(schoolContextService.getSchoolId(), memberId)));
    }
}
