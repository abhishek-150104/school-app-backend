package com.school.school_app.dto.request;

import com.school.school_app.entity.AttendanceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BulkMarkAttendanceRequest {

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotEmpty(message = "Attendance records are required")
    @Valid
    private List<AttendanceEntry> records;

    @Data
    public static class AttendanceEntry {

        @NotNull(message = "Student ID is required")
        private String studentId;

        @NotNull(message = "Status is required")
        private AttendanceStatus status;

        private String remarks;
    }
}
