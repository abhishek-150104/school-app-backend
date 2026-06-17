package com.school.school_app.dto.request;

import com.school.school_app.entity.Address;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateStaffRequest {

    // Optional: if provided, links an existing TEACHER-role user account.
    // If null, a new user account is auto-created with employeeId as login.
    private String userId;

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String designation;
    private List<String> subjects;
    private String qualification;
    private LocalDate joiningDate;
    private Address address;

    // For recovery (used when auto-creating user account)
    private String email;
    private String phone;
}
