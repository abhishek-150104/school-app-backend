package com.school.school_app.repository;

import com.school.school_app.entity.Staff;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends MongoRepository<Staff, String> {

    Optional<Staff> findByIdAndSchoolId(String id, String schoolId);

    Optional<Staff> findByUserId(String userId);

    Optional<Staff> findByUserIdAndSchoolId(String userId, String schoolId);

    List<Staff> findBySchoolIdAndActiveTrue(String schoolId);

    boolean existsBySchoolIdAndEmployeeId(String schoolId, String employeeId);

    boolean existsByUserId(String userId);

    @Query("{'schoolId': ?0, 'active': true, $or: [{'fullName': {$regex: ?1, $options: 'i'}}, {'employeeId': {$regex: ?1, $options: 'i'}}]}")
    List<Staff> searchByNameOrEmployeeId(String schoolId, String query);
}
