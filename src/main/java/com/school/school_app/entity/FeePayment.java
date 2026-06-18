package com.school.school_app.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fee_payments")
@CompoundIndexes({
        @CompoundIndex(def = "{'schoolId': 1, 'invoiceId': 1}"),
        @CompoundIndex(def = "{'schoolId': 1, 'studentId': 1}")
})
public class FeePayment {

    @Id
    private String id;

    private String schoolId;

    private String invoiceId;
    private String studentId;
    private String studentFullName;

    private BigDecimal amount;
    private String paymentMode; // CASH, ONLINE, CHEQUE, DD
    private String transactionId;
    private String remarks;

    private String collectedById;
    private String collectedByName;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
