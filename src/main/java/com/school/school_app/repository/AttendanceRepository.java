package com.school.school_app.repository;

import com.school.school_app.entity.AttendanceRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends MongoRepository<AttendanceRecord, String> {

    List<AttendanceRecord> findBySchoolIdAndSectionIdAndDate(
            String schoolId, String sectionId, LocalDate date);

    Optional<AttendanceRecord> findBySchoolIdAndSectionIdAndDateAndStudentId(
            String schoolId, String sectionId, LocalDate date, String studentId);

    List<AttendanceRecord> findBySchoolIdAndSectionIdAndDateBetween(
            String schoolId, String sectionId, LocalDate from, LocalDate to);

    List<AttendanceRecord> findBySchoolIdAndStudentIdAndDateBetween(
            String schoolId, String studentId, LocalDate from, LocalDate to);

    Optional<AttendanceRecord> findByIdAndSchoolId(String id, String schoolId);
}
