package com.school.school_app.dto.response;

import com.school.school_app.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileResponse {
    private String id;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private String profilePhotoUrl;
    private LocalDateTime createdAt;
}
