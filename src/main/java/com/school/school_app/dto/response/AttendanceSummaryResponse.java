package com.school.school_app.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class AttendanceSummaryResponse {

    private String sectionId;
    private String sectionName;
    private String classRoomName;
    private LocalDate from;
    private LocalDate to;
    private int totalDays;
    private List<StudentAttendanceStat> students;

    @Data
    @Builder
    public static class StudentAttendanceStat {
        private String studentId;
        private String fullName;
        private String admissionNumber;
        private int present;
        private int absent;
        private int late;
        private int excused;
        private double percentage;
    }
}
