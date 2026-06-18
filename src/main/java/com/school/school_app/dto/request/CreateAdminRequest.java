package com.school.school_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAdminRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String email;
    private String phone;

    @NotBlank(message = "Password is required")
    private String password;
}
