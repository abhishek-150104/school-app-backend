package com.school.school_app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSchoolRequest {

    @NotBlank
    private String name;

    private String address;
    private String city;
    private String state;
    private String pincode;

    private String phone;

    @Email
    private String email;

    private String logoUrl;
    private String website;
    private String affiliationNumber;
    private String board;
}
