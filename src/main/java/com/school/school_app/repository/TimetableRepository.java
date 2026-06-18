package com.school.school_app.repository;

import com.school.school_app.entity.TimetableEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TimetableRepository extends MongoRepository<TimetableEntry, String> {
    List<TimetableEntry> findBySchoolIdAndSectionId(String schoolId, String sectionId);
    List<TimetableEntry> findBySchoolIdAndTeacherId(String schoolId, String teacherId);
    List<TimetableEntry> findBySchoolIdAndSectionIdAndDayOfWeek(String schoolId, String sectionId, String dayOfWeek);
    boolean existsBySchoolIdAndSectionIdAndDayOfWeekAndPeriodNumber(
            String schoolId, String sectionId, String dayOfWeek, int periodNumber);
    void deleteBySchoolIdAndSectionId(String schoolId, String sectionId);
}
