package com.school.school_app.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "staff")
@CompoundIndexes({
        @CompoundIndex(def = "{'schoolId': 1, 'employeeId': 1}", unique = true)
})
public class Staff {

    @Id
    private String id;

    private String schoolId;
    private String schoolName;

    @Indexed
    private String userId;

    private String employeeId;

    private String firstName;
    private String lastName;
    @Indexed
    private String fullName;

    private String designation;
    private List<String> subjects;
    private String qualification;
    private LocalDate joiningDate;

    private String profilePhotoUrl;
    private Address address;

    private boolean active;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
