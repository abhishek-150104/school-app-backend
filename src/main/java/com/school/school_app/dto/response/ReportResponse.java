package com.school.school_app.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ReportResponse {
    private String schoolId;
    private String reportType;
    private Map<String, Object> data;
    private String generatedAt;
}
