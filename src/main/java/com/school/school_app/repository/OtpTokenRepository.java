package com.school.school_app.repository;

import com.school.school_app.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findTopByPhoneAndUsedFalseOrderByCreatedAtDesc(String phone);
    void deleteAllByPhone(String phone);
}
