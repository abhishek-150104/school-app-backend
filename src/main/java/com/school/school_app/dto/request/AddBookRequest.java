package com.school.school_app.dto.request;

import lombok.Data;

@Data
public class AddBookRequest {
    private String title;
    private String author;
    private String isbn;
    private String category;
    private String publisher;
    private int publishYear;
    private int totalCopies;
}
