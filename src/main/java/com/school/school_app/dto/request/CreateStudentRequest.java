package com.school.school_app.dto.request;

import com.school.school_app.entity.Address;
import com.school.school_app.entity.Gender;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateStudentRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    private Gender gender;

    @NotBlank
    private String admissionNumber;

    @Min(1)
    private int rollNumber;

    @NotNull
    private LocalDate admissionDate;

    @NotBlank
    private String academicYearId;

    @NotBlank
    private String classRoomId;

    @NotBlank
    private String sectionId;

    private String bloodGroup;
    private String religion;
    private String category;
    private Address address;

    // Optional — can be linked later via /link-parent
    private String parentId;
}
