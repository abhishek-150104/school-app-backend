package com.school.school_app.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class IssueBookRequest {
    private String bookId;
    private String memberId;
    private String memberRole;
    private LocalDate dueDate;
}
