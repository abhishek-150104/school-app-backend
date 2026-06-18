package com.school.school_app.repository;

import com.school.school_app.entity.OtpToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OtpTokenRepository extends MongoRepository<OtpToken, String> {
    Optional<OtpToken> findTopByPhoneAndUsedFalseOrderByCreatedAtDesc(String phone);
    void deleteAllByPhone(String phone);
}
