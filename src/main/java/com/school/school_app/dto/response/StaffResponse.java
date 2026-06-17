package com.school.school_app.dto.response;

import com.school.school_app.entity.Address;
import com.school.school_app.entity.Staff;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class StaffResponse {

    private String id;
    private String schoolId;
    private String schoolName;
    private String userId;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String designation;
    private List<String> subjects;
    private String qualification;
    private LocalDate joiningDate;
    private String profilePhotoUrl;
    private Address address;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static StaffResponse from(Staff s) {
        return StaffResponse.builder()
                .id(s.getId())
                .schoolId(s.getSchoolId())
                .schoolName(s.getSchoolName())
                .userId(s.getUserId())
                .employeeId(s.getEmployeeId())
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .fullName(s.getFullName())
                .designation(s.getDesignation())
                .subjects(s.getSubjects())
                .qualification(s.getQualification())
                .joiningDate(s.getJoiningDate())
                .profilePhotoUrl(s.getProfilePhotoUrl())
                .address(s.getAddress())
                .active(s.isActive())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
