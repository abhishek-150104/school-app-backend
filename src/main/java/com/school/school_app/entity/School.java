package com.school.school_app.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "schools")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class School {

    @Id
    private String id;

    private String name;
    private String address;
    private String city;
    private String state;
    private String pincode;

    @Indexed(unique = true, sparse = true)
    private String phone;

    @Indexed(unique = true, sparse = true)
    private String email;

    private String logoUrl;
    private String website;
    private String affiliationNumber;
    private String board;

    @Builder.Default
    private boolean active = true;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
