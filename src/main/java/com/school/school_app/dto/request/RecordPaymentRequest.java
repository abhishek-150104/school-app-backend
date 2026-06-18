package com.school.school_app.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecordPaymentRequest {
    private String invoiceId;
    private BigDecimal amount;
    private String paymentMode;
    private String transactionId;
    private String remarks;
}
