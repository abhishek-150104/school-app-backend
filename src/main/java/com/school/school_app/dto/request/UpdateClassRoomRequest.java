package com.school.school_app.dto.request;

import lombok.Data;

@Data
public class UpdateClassRoomRequest {
    private String name;
    private Integer displayOrder;
}
