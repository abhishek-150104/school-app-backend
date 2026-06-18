package com.school.school_app.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateHomeworkRequest {

    private String title;
    private String description;
    private String subject;
    private LocalDate dueDate;
}
