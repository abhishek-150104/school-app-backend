package com.school.school_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateHomeworkRequest {

    @NotBlank
    private String sectionId;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String subject;

    @NotNull
    private LocalDate dueDate;
}
