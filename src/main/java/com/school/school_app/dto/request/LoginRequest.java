package com.school.school_app.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    @JsonAlias("identifier")
    private String username; // email, phone, admissionNumber, or employeeId

    @NotBlank
    private String password;
}
