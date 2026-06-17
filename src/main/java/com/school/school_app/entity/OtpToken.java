package com.school.school_app.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "otp_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpToken {

    @Id
    private String id;

    private String phone;

    private String otp;

    private LocalDateTime expiresAt;

    @Builder.Default
    private boolean used = false;

    @CreatedDate
    private LocalDateTime createdAt;
}
