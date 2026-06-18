package com.school.school_app.dto.response;

import com.school.school_app.entity.LibraryBook;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LibraryBookResponse {
    private String id;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private String publisher;
    private int publishYear;
    private int totalCopies;
    private int availableCopies;

    public static LibraryBookResponse from(LibraryBook b) {
        return LibraryBookResponse.builder()
                .id(b.getId()).title(b.getTitle()).author(b.getAuthor())
                .isbn(b.getIsbn()).category(b.getCategory()).publisher(b.getPublisher())
                .publishYear(b.getPublishYear()).totalCopies(b.getTotalCopies())
                .availableCopies(b.getAvailableCopies())
                .build();
    }
}
