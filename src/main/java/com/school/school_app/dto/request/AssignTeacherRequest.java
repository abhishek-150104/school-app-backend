package com.school.school_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignTeacherRequest {

    @NotBlank
    private String teacherId;
}
