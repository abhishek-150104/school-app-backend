package com.school.school_app.dto.response;

import com.school.school_app.entity.FeeStructure;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class FeeStructureResponse {
    private String id;
    private String schoolId;
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
    private LocalDateTime createdAt;

    public static FeeStructureResponse from(FeeStructure f) {
        return FeeStructureResponse.builder()
                .id(f.getId())
                .schoolId(f.getSchoolId())
                .academicYearId(f.getAcademicYearId())
                .academicYearName(f.getAcademicYearName())
                .classRoomId(f.getClassRoomId())
                .classRoomName(f.getClassRoomName())
                .tuitionFee(f.getTuitionFee())
                .examFee(f.getExamFee())
                .libraryFee(f.getLibraryFee())
                .sportsFee(f.getSportsFee())
                .miscFee(f.getMiscFee())
                .totalFee(f.getTotalFee())
                .createdAt(f.getCreatedAt())
                .build();
    }
}
