package com.school.school_app.service;

import com.school.school_app.dto.request.CreateTimetableEntryRequest;
import com.school.school_app.dto.response.TimetableEntryResponse;
import com.school.school_app.entity.*;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimetableServiceTest {

    @Mock TimetableRepository timetableRepo;
    @Mock SectionRepository sectionRepo;
    @Mock SubjectRepository subjectRepo;
    @Mock StaffRepository staffRepo;

    @InjectMocks TimetableService timetableService;

    private static final String SCHOOL_ID = "school1";

    @Test
    void addEntry_success() {
        CreateTimetableEntryRequest req = new CreateTimetableEntryRequest();
        req.setSectionId("sec1");
        req.setSubjectId("sub1");
        req.setTeacherId("staff1");
        req.setDayOfWeek("MONDAY");
        req.setPeriodNumber(1);
        req.setStartTime("08:00");
        req.setEndTime("08:45");

        Section section = Section.builder().id("sec1").classRoomId("cr1").classRoomName("Class 1").name("A").build();
        Subject subject = Subject.builder().id("sub1").name("Math").build();
        Staff teacher = Staff.builder().id("staff1").fullName("Mr. John").build();
        TimetableEntry saved = TimetableEntry.builder().id("tt1").sectionId("sec1").dayOfWeek("MONDAY").periodNumber(1).build();

        when(timetableRepo.existsBySchoolIdAndSectionIdAndDayOfWeekAndPeriodNumber(any(), any(), any(), anyInt())).thenReturn(false);
        when(sectionRepo.findById("sec1")).thenReturn(Optional.of(section));
        when(subjectRepo.findById("sub1")).thenReturn(Optional.of(subject));
        when(staffRepo.findById("staff1")).thenReturn(Optional.of(teacher));
        when(timetableRepo.save(any())).thenReturn(saved);

        TimetableEntryResponse result = timetableService.addEntry(SCHOOL_ID, "Test", req, "u1", "Admin");

        assertThat(result.getId()).isEqualTo("tt1");
        assertThat(result.getDayOfWeek()).isEqualTo("MONDAY");
    }

    @Test
    void addEntry_slotConflictThrows() {
        CreateTimetableEntryRequest req = new CreateTimetableEntryRequest();
        req.setSectionId("sec1");
        req.setDayOfWeek("MONDAY");
        req.setPeriodNumber(1);

        when(timetableRepo.existsBySchoolIdAndSectionIdAndDayOfWeekAndPeriodNumber(any(), any(), any(), anyInt())).thenReturn(true);

        assertThatThrownBy(() -> timetableService.addEntry(SCHOOL_ID, "Test", req, "u1", "Admin"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("already occupied");
    }

    @Test
    void getSectionTimetable_returnsEntries() {
        TimetableEntry e = TimetableEntry.builder().id("tt1").sectionId("sec1").build();
        when(timetableRepo.findBySchoolIdAndSectionId(SCHOOL_ID, "sec1")).thenReturn(List.of(e));

        List<TimetableEntryResponse> result = timetableService.getSectionTimetable(SCHOOL_ID, "sec1");

        assertThat(result).hasSize(1);
    }

    @Test
    void getTeacherTimetable_returnsEntries() {
        TimetableEntry e = TimetableEntry.builder().id("tt1").teacherId("staff1").build();
        when(timetableRepo.findBySchoolIdAndTeacherId(SCHOOL_ID, "staff1")).thenReturn(List.of(e));

        List<TimetableEntryResponse> result = timetableService.getTeacherTimetable(SCHOOL_ID, "staff1");

        assertThat(result).hasSize(1);
    }

    @Test
    void getTeacherTimetableByUser_returnsEntries() {
        Staff staff = Staff.builder().id("staff1").userId("u1").build();
        TimetableEntry e = TimetableEntry.builder().id("tt1").teacherId("staff1").build();
        when(staffRepo.findByUserIdAndSchoolId("u1", SCHOOL_ID)).thenReturn(Optional.of(staff));
        when(timetableRepo.findBySchoolIdAndTeacherId(SCHOOL_ID, "staff1")).thenReturn(List.of(e));

        List<TimetableEntryResponse> result = timetableService.getTeacherTimetableByUser(SCHOOL_ID, "u1");

        assertThat(result).hasSize(1);
    }

    @Test
    void getTeacherTimetableByUser_staffNotFound_returnsEmpty() {
        when(staffRepo.findByUserIdAndSchoolId("u1", SCHOOL_ID)).thenReturn(Optional.empty());

        List<TimetableEntryResponse> result = timetableService.getTeacherTimetableByUser(SCHOOL_ID, "u1");

        assertThat(result).isEmpty();
    }

    @Test
    void deleteEntry_success() {
        TimetableEntry e = TimetableEntry.builder().id("tt1").schoolId(SCHOOL_ID).build();
        when(timetableRepo.findById("tt1")).thenReturn(Optional.of(e));

        timetableService.deleteEntry(SCHOOL_ID, "tt1");

        verify(timetableRepo).delete(e);
    }

    @Test
    void deleteEntry_notFoundThrows() {
        when(timetableRepo.findById("tt1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> timetableService.deleteEntry(SCHOOL_ID, "tt1"))
                .isInstanceOf(AppException.class);
    }

    @Test
    void addEntry_sectionNotFoundThrows() {
        CreateTimetableEntryRequest req = new CreateTimetableEntryRequest();
        req.setSectionId("sec1");
        req.setDayOfWeek("MONDAY");
        req.setPeriodNumber(1);

        when(timetableRepo.existsBySchoolIdAndSectionIdAndDayOfWeekAndPeriodNumber(any(), any(), any(), anyInt())).thenReturn(false);
        when(sectionRepo.findById("sec1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> timetableService.addEntry(SCHOOL_ID, "Test", req, "u1", "Admin"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Section not found");
    }
}
