package com.school.school_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TeacherProfileResponse {
    private StaffResponse profile;
    private List<SectionResponse> assignedSections;
}
