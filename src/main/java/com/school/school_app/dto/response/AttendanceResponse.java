package com.school.school_app.dto.response;

import com.school.school_app.entity.AttendanceRecord;
import com.school.school_app.entity.AttendanceStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class AttendanceResponse {

    private String id;
    private String schoolId;
    private String classRoomId;
    private String classRoomName;
    private String sectionId;
    private String sectionName;
    private LocalDate date;
    private String studentId;
    private String studentFullName;
    private String admissionNumber;
    private AttendanceStatus status;
    private String markedById;
    private String markedByName;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AttendanceResponse from(AttendanceRecord r) {
        return AttendanceResponse.builder()
                .id(r.getId())
                .schoolId(r.getSchoolId())
                .classRoomId(r.getClassRoomId())
                .classRoomName(r.getClassRoomName())
                .sectionId(r.getSectionId())
                .sectionName(r.getSectionName())
                .date(r.getDate())
                .studentId(r.getStudentId())
                .studentFullName(r.getStudentFullName())
                .admissionNumber(r.getAdmissionNumber())
                .status(r.getStatus())
                .markedById(r.getMarkedById())
                .markedByName(r.getMarkedByName())
                .remarks(r.getRemarks())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
