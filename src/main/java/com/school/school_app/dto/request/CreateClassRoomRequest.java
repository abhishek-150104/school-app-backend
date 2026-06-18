package com.school.school_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateClassRoomRequest {

    @NotBlank
    private String name;

    @NotNull
    private String academicYearId;

    private int displayOrder = 0;
}
