package com.school.school_app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "students")
@CompoundIndexes({
        @CompoundIndex(def = "{'schoolId': 1, 'admissionNumber': 1}", unique = true),
        @CompoundIndex(def = "{'schoolId': 1, 'sectionId': 1, 'rollNumber': 1}", unique = true)
})
public class Student {

    @Id
    private String id;

    // School / Class placement (denormalized snapshots for fast reads)
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

    // Personal info
    private String firstName;
    private String lastName;
    @Indexed
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String bloodGroup;
    private String religion;
    private String category;       // GENERAL, OBC, SC, ST, EWS
    private String profilePhotoUrl;

    // Contact / Address
    private Address address;

    // Parent link (denormalized)
    @Indexed
    private String parentId;
    private String parentName;
    private String parentPhone;

    private boolean active;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
