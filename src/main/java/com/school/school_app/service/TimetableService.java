package com.school.school_app.service;

import com.school.school_app.dto.request.CreateTimetableEntryRequest;
import com.school.school_app.dto.response.TimetableEntryResponse;
import com.school.school_app.entity.*;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TimetableRepository timetableRepo;
    private final SectionRepository sectionRepo;
    private final SubjectRepository subjectRepo;
    private final StaffRepository staffRepo;

    public TimetableEntryResponse addEntry(String schoolId, String schoolName,
                                           CreateTimetableEntryRequest req,
                                           String creatorId, String creatorName) {
        if (timetableRepo.existsBySchoolIdAndSectionIdAndDayOfWeekAndPeriodNumber(
                schoolId, req.getSectionId(), req.getDayOfWeek(), req.getPeriodNumber())) {
            throw new AppException("Slot already occupied", HttpStatus.CONFLICT);
        }

        Section section = sectionRepo.findById(req.getSectionId())
                .orElseThrow(() -> new AppException("Section not found", HttpStatus.NOT_FOUND));
        Subject subject = subjectRepo.findById(req.getSubjectId())
                .orElseThrow(() -> new AppException("Subject not found", HttpStatus.NOT_FOUND));
        Staff teacher = staffRepo.findById(req.getTeacherId())
                .orElseThrow(() -> new AppException("Teacher not found", HttpStatus.NOT_FOUND));

        TimetableEntry entry = TimetableEntry.builder()
                .schoolId(schoolId).schoolName(schoolName)
                .classRoomId(section.getClassRoomId()).classRoomName(section.getClassRoomName())
                .sectionId(section.getId()).sectionName(section.getName())
                .subjectId(subject.getId()).subjectName(subject.getName())
                .teacherId(teacher.getId()).teacherName(teacher.getFullName())
                .dayOfWeek(req.getDayOfWeek())
                .periodNumber(req.getPeriodNumber())
                .startTime(req.getStartTime()).endTime(req.getEndTime())
                .createdById(creatorId).createdByName(creatorName)
                .build();

        return TimetableEntryResponse.from(timetableRepo.save(entry));
    }

    public List<TimetableEntryResponse> getSectionTimetable(String schoolId, String sectionId) {
        return timetableRepo.findBySchoolIdAndSectionId(schoolId, sectionId)
                .stream().map(TimetableEntryResponse::from).toList();
    }

    public List<TimetableEntryResponse> getTeacherTimetable(String schoolId, String teacherId) {
        return timetableRepo.findBySchoolIdAndTeacherId(schoolId, teacherId)
                .stream().map(TimetableEntryResponse::from).toList();
    }

    public List<TimetableEntryResponse> getTeacherTimetableByUser(String schoolId, String userId) {
        return staffRepo.findByUserIdAndSchoolId(userId, schoolId)
                .map(s -> timetableRepo.findBySchoolIdAndTeacherId(schoolId, s.getId())
                        .stream().map(TimetableEntryResponse::from).toList())
                .orElse(List.of());
    }

    public void deleteEntry(String schoolId, String entryId) {
        TimetableEntry entry = timetableRepo.findById(entryId)
                .filter(e -> e.getSchoolId().equals(schoolId))
                .orElseThrow(() -> new AppException("Timetable entry not found", HttpStatus.NOT_FOUND));
        timetableRepo.delete(entry);
    }
}
