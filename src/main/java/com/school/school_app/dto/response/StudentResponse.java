package com.school.school_app.dto.response;

import com.school.school_app.entity.Address;
import com.school.school_app.entity.Gender;
import com.school.school_app.entity.Student;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class StudentResponse {

    private String id;

    // Placement
    private String schoolId;
    private String schoolName;
    private String academicYearId;
    private String academicYearLabel;
    private String classRoomId;
    private String classRoomName;
    private String sectionId;
    private String sectionName;

    // Admission
    private String admissionNumber;
    private int rollNumber;
    private LocalDate admissionDate;

    // Personal
    private String firstName;
    private String lastName;
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String bloodGroup;
    private String religion;
    private String category;
    private String profilePhotoUrl;

    // Contact
    private Address address;

    // Parent
    private String parentId;
    private String parentName;
    private String parentPhone;

    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static StudentResponse from(Student s) {
        return StudentResponse.builder()
                .id(s.getId())
                .schoolId(s.getSchoolId())
                .schoolName(s.getSchoolName())
                .academicYearId(s.getAcademicYearId())
                .academicYearLabel(s.getAcademicYearLabel())
                .classRoomId(s.getClassRoomId())
                .classRoomName(s.getClassRoomName())
                .sectionId(s.getSectionId())
                .sectionName(s.getSectionName())
                .admissionNumber(s.getAdmissionNumber())
                .rollNumber(s.getRollNumber())
                .admissionDate(s.getAdmissionDate())
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .fullName(s.getFullName())
                .dateOfBirth(s.getDateOfBirth())
                .gender(s.getGender())
                .bloodGroup(s.getBloodGroup())
                .religion(s.getReligion())
                .category(s.getCategory())
                .profilePhotoUrl(s.getProfilePhotoUrl())
                .address(s.getAddress())
                .parentId(s.getParentId())
                .parentName(s.getParentName())
                .parentPhone(s.getParentPhone())
                .active(s.isActive())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
