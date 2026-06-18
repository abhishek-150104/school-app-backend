package com.school.school_app.repository;

import com.school.school_app.entity.CircularRead;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface CircularReadRepository extends MongoRepository<CircularRead, String> {
    Optional<CircularRead> findByCircularIdAndUserId(String circularId, String userId);
    List<CircularRead> findByUserId(String userId);
    boolean existsByCircularIdAndUserId(String circularId, String userId);
}
