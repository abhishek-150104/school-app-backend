package com.school.school_app.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EnrollStudentResponse {
    private StudentResponse student;
    private String loginId;     // admission number — student uses this to log in
    private String tempPassword; // shown once; student must change on first login
}
