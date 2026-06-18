package com.school.school_app.dto.response;

import com.school.school_app.entity.FeePayment;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class FeePaymentResponse {
    private String id;
    private String invoiceId;
    private String studentId;
    private String studentFullName;
    private BigDecimal amount;
    private String paymentMode;
    private String transactionId;
    private String remarks;
    private String collectedByName;
    private LocalDateTime createdAt;

    public static FeePaymentResponse from(FeePayment p) {
        return FeePaymentResponse.builder()
                .id(p.getId())
                .invoiceId(p.getInvoiceId())
                .studentId(p.getStudentId())
                .studentFullName(p.getStudentFullName())
                .amount(p.getAmount())
                .paymentMode(p.getPaymentMode())
                .transactionId(p.getTransactionId())
                .remarks(p.getRemarks())
                .collectedByName(p.getCollectedByName())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
