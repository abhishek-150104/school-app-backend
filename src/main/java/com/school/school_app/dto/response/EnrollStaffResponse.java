package com.school.school_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollStaffResponse {
    private StaffResponse staff;
    private String loginId;
    private String tempPassword;
}
