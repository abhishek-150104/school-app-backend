package com.school.school_app.dto.request;

import com.school.school_app.entity.AttendanceStatus;
import lombok.Data;

@Data
public class UpdateAttendanceRequest {
    private AttendanceStatus status;
    private String remarks;
}
