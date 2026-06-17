package com.school.school_app.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSectionRequest {

    @NotBlank
    private String name; // e.g. "A", "B", "C"

    @Min(1)
    private int capacity = 40;
}
