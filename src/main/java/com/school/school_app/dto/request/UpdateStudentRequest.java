package com.school.school_app.dto.request;

import com.school.school_app.entity.Address;
import com.school.school_app.entity.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateStudentRequest {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private Integer rollNumber;
    private String bloodGroup;
    private String religion;
    private String category;
    private String profilePhotoUrl;
    private Address address;
}
