package com.school.school_app.dto.response;

import com.school.school_app.entity.School;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SchoolResponse {

    private String id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String phone;
    private String email;
    private String logoUrl;
    private String website;
    private String affiliationNumber;
    private String board;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SchoolResponse from(School school) {
        return SchoolResponse.builder()
                .id(school.getId())
                .name(school.getName())
                .address(school.getAddress())
                .city(school.getCity())
                .state(school.getState())
                .pincode(school.getPincode())
                .phone(school.getPhone())
                .email(school.getEmail())
                .logoUrl(school.getLogoUrl())
                .website(school.getWebsite())
                .affiliationNumber(school.getAffiliationNumber())
                .board(school.getBoard())
                .active(school.isActive())
                .createdAt(school.getCreatedAt())
                .updatedAt(school.getUpdatedAt())
                .build();
    }
}
