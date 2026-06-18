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
@Document(collection = "fee_structures")
@CompoundIndexes({
        @CompoundIndex(def = "{'schoolId': 1, 'academicYearId': 1, 'classRoomId': 1}", unique = true)
})
public class FeeStructure {

    @Id
    private String id;

    private String schoolId;
    private String schoolName;

    private String academicYearId;
    private String academicYearName;

    private String classRoomId;
    private String classRoomName;

    private BigDecimal tuitionFee;
    private BigDecimal examFee;
    private BigDecimal libraryFee;
    private BigDecimal sportsFee;
    private BigDecimal miscFee;
    private BigDecimal totalFee;

    private String createdById;
    private String createdByName;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
