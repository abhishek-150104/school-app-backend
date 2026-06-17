package com.school.school_app.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAcademicYearRequest {

    @NotBlank
    private String label; // e.g. "2024-25"

    @NotNull
    @Min(2000)
    private Integer startYear;

    @NotNull
    @Min(2000)
    private Integer endYear;

    private boolean active = false;
}
