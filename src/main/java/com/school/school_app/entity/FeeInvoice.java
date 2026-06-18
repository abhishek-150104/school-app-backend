package com.school.school_app.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fee_invoices")
@CompoundIndexes({
        @CompoundIndex(def = "{'schoolId': 1, 'studentId': 1, 'academicYearId': 1}"),
        @CompoundIndex(def = "{'schoolId': 1, 'status': 1}")
})
public class FeeInvoice {

    @Id
    private String id;

    private String schoolId;
    private String schoolName;

    private String studentId;
    private String studentFullName;
    private String admissionNumber;

    private String classRoomId;
    private String classRoomName;
    private String sectionId;
    private String sectionName;

    private String academicYearId;
    private String academicYearName;

    private String feeStructureId;

    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal dueAmount;

    private FeeStatus status; // PENDING, PARTIAL, PAID, OVERDUE

    private LocalDate dueDate;

    private String createdById;
    private String createdByName;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
