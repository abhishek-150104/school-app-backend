package com.school.school_app.dto.response;

import com.school.school_app.entity.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String userId;
    private String fullName;
    private Role role;
}
