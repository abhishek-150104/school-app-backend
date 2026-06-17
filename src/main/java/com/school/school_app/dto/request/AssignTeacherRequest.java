package com.school.school_app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignTeacherRequest {

    @NotNull
    private Long teacherId;
}
