package com.school.school_app.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateFeeInvoiceRequest {
    private String studentId;
    private String feeStructureId;
    private LocalDate dueDate;
}
