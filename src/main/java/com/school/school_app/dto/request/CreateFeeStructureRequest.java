package com.school.school_app.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateFeeStructureRequest {
    private String academicYearId;
    private String classRoomId;
    private BigDecimal tuitionFee;
    private BigDecimal examFee;
    private BigDecimal libraryFee;
    private BigDecimal sportsFee;
    private BigDecimal miscFee;
}
