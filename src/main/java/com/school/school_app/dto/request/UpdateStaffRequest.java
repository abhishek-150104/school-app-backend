package com.school.school_app.dto.request;

import com.school.school_app.entity.Address;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateStaffRequest {
    private String firstName;
    private String lastName;
    private String designation;
    private List<String> subjects;
    private String qualification;
    private LocalDate joiningDate;
    private String profilePhotoUrl;
    private Address address;
}
