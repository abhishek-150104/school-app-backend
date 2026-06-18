package com.school.school_app.dto.response;

import com.school.school_app.entity.FeeInvoice;
import com.school.school_app.entity.FeeStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class FeeInvoiceResponse {
    private String id;
    private String studentId;
    private String studentFullName;
    private String admissionNumber;
    private String classRoomName;
    private String sectionName;
    private String academicYearName;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal dueAmount;
    private FeeStatus status;
    private LocalDate dueDate;
    private LocalDateTime createdAt;

    public static FeeInvoiceResponse from(FeeInvoice inv) {
        return FeeInvoiceResponse.builder()
                .id(inv.getId())
                .studentId(inv.getStudentId())
                .studentFullName(inv.getStudentFullName())
                .admissionNumber(inv.getAdmissionNumber())
                .classRoomName(inv.getClassRoomName())
                .sectionName(inv.getSectionName())
                .academicYearName(inv.getAcademicYearName())
                .totalAmount(inv.getTotalAmount())
                .paidAmount(inv.getPaidAmount())
                .dueAmount(inv.getDueAmount())
                .status(inv.getStatus())
                .dueDate(inv.getDueDate())
                .createdAt(inv.getCreatedAt())
                .build();
    }
}
