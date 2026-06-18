package com.school.school_app.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TransferStudentRequest {

    @NotBlank
    private String classRoomId;

    @NotBlank
    private String sectionId;

    @Min(1)
    private int rollNumber;
}
