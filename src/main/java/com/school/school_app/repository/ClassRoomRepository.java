package com.school.school_app.repository;

import com.school.school_app.entity.ClassRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassRoomRepository extends JpaRepository<ClassRoom, Long> {

    List<ClassRoom> findBySchoolIdOrderByDisplayOrderAscNameAsc(Long schoolId);

    List<ClassRoom> findBySchoolIdAndAcademicYearIdOrderByDisplayOrderAscNameAsc(Long schoolId, Long academicYearId);

    Optional<ClassRoom> findByIdAndSchoolId(Long id, Long schoolId);

    boolean existsBySchoolIdAndAcademicYearIdAndName(Long schoolId, Long academicYearId, String name);
}
