package com.school.school_app.repository;

import com.school.school_app.entity.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {

    List<AcademicYear> findBySchoolIdOrderByStartYearDesc(Long schoolId);

    Optional<AcademicYear> findBySchoolIdAndActiveTrue(Long schoolId);

    boolean existsBySchoolIdAndLabel(Long schoolId, String label);

    // deactivate all years for a school before activating a new one
    @Modifying
    @Query("UPDATE AcademicYear ay SET ay.active = false WHERE ay.school.id = :schoolId")
    void deactivateAllBySchoolId(@Param("schoolId") Long schoolId);
}
