package com.school.school_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCircularRequest {
    @NotBlank private String title;
    @NotBlank private String content;
    @NotBlank private String targetType; // ALL, CLASS, SECTION
    private String targetClassRoomId;
    private String targetSectionId;
}
