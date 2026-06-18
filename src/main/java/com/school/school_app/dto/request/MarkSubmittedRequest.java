package com.school.school_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MarkSubmittedRequest {

    @NotBlank
    private String studentId;

    @NotBlank
    private String admissionNumber;

    @NotBlank
    private String studentFullName;

    private String remarks;
}
