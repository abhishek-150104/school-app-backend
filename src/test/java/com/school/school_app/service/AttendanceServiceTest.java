package com.school.school_app.service;

import com.school.school_app.dto.request.BulkMarkAttendanceRequest;
import com.school.school_app.dto.request.UpdateAttendanceRequest;
import com.school.school_app.dto.response.AttendanceResponse;
import com.school.school_app.dto.response.AttendanceSummaryResponse;
import com.school.school_app.entity.*;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.AttendanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock AttendanceRepository attendanceRepository;
    @Mock SectionService sectionService;
    @Mock ClassRoomService classRoomService;
    @Mock StudentService studentService;

    @InjectMocks AttendanceService attendanceService;

    private Section section;
    private Student student;
    private AttendanceRecord record;
    private final String SCHOOL_ID   = "school1";
    private final String SECTION_ID  = "section1";
    private final String STUDENT_ID  = "student1";
    private final LocalDate DATE     = LocalDate.of(2025, 6, 1);

    @BeforeEach
    void setUp() {
        section = new Section();
        section.setId(SECTION_ID);
        section.setName("A");
        section.setClassRoomId("class1");
        section.setClassRoomName("Grade 5");

        student = new Student();
        student.setId(STUDENT_ID);
        student.setFullName("Alice Smith");
        student.setAdmissionNumber("ADM001");
        student.setSchoolId(SCHOOL_ID);

        record = AttendanceRecord.builder()
                .id("att1")
                .schoolId(SCHOOL_ID)
                .sectionId(SECTION_ID)
                .classRoomId("class1")
                .classRoomName("Grade 5")
                .sectionName("A")
                .date(DATE)
                .studentId(STUDENT_ID)
                .studentFullName("Alice Smith")
                .admissionNumber("ADM001")
                .status(AttendanceStatus.PRESENT)
                .markedById("teacher1")
                .markedByName("Mr. John")
                .build();
    }

    @Test
    void markBulk_createsNewRecord_whenNotExists() {
        BulkMarkAttendanceRequest req = new BulkMarkAttendanceRequest();
        req.setDate(DATE);
        BulkMarkAttendanceRequest.AttendanceEntry entry = new BulkMarkAttendanceRequest.AttendanceEntry();
        entry.setStudentId(STUDENT_ID);
        entry.setStatus(AttendanceStatus.PRESENT);
        req.setRecords(List.of(entry));

        when(sectionService.findById(SECTION_ID)).thenReturn(section);
        when(studentService.findByIdAndSchool(STUDENT_ID, SCHOOL_ID)).thenReturn(student);
        when(attendanceRepository.findBySchoolIdAndSectionIdAndDateAndStudentId(
                SCHOOL_ID, SECTION_ID, DATE, STUDENT_ID)).thenReturn(Optional.empty());
        when(attendanceRepository.findBySchoolIdAndSectionIdAndDate(SCHOOL_ID, SECTION_ID, DATE))
                .thenReturn(List.of(record));

        List<AttendanceResponse> result = attendanceService.markBulk(
                SCHOOL_ID, SECTION_ID, req, "teacher1", "Mr. John");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStudentId()).isEqualTo(STUDENT_ID);
        verify(attendanceRepository).save(any(AttendanceRecord.class));
    }

    @Test
    void markBulk_updatesExistingRecord_whenExists() {
        BulkMarkAttendanceRequest req = new BulkMarkAttendanceRequest();
        req.setDate(DATE);
        BulkMarkAttendanceRequest.AttendanceEntry entry = new BulkMarkAttendanceRequest.AttendanceEntry();
        entry.setStudentId(STUDENT_ID);
        entry.setStatus(AttendanceStatus.ABSENT);
        req.setRecords(List.of(entry));

        when(sectionService.findById(SECTION_ID)).thenReturn(section);
        when(studentService.findByIdAndSchool(STUDENT_ID, SCHOOL_ID)).thenReturn(student);
        when(attendanceRepository.findBySchoolIdAndSectionIdAndDateAndStudentId(
                SCHOOL_ID, SECTION_ID, DATE, STUDENT_ID)).thenReturn(Optional.of(record));
        when(attendanceRepository.findBySchoolIdAndSectionIdAndDate(SCHOOL_ID, SECTION_ID, DATE))
                .thenReturn(List.of(record));

        attendanceService.markBulk(SCHOOL_ID, SECTION_ID, req, "teacher1", "Mr. John");

        verify(attendanceRepository).save(record);
        assertThat(record.getStatus()).isEqualTo(AttendanceStatus.ABSENT);
    }

    @Test
    void getBySection_returnsRecords() {
        when(sectionService.findById(SECTION_ID)).thenReturn(section);
        when(attendanceRepository.findBySchoolIdAndSectionIdAndDate(SCHOOL_ID, SECTION_ID, DATE))
                .thenReturn(List.of(record));

        List<AttendanceResponse> result = attendanceService.getBySection(SCHOOL_ID, SECTION_ID, DATE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(AttendanceStatus.PRESENT);
    }

    @Test
    void getSummary_computesStatsCorrectly() {
        AttendanceRecord r2 = AttendanceRecord.builder()
                .schoolId(SCHOOL_ID).sectionId(SECTION_ID)
                .classRoomId("class1").classRoomName("Grade 5").sectionName("A")
                .date(DATE.plusDays(1))
                .studentId(STUDENT_ID).studentFullName("Alice Smith").admissionNumber("ADM001")
                .status(AttendanceStatus.ABSENT)
                .build();

        when(sectionService.findById(SECTION_ID)).thenReturn(section);
        when(attendanceRepository.findBySchoolIdAndSectionIdAndDateBetween(
                SCHOOL_ID, SECTION_ID, DATE, DATE.plusDays(6)))
                .thenReturn(List.of(record, r2));

        AttendanceSummaryResponse summary = attendanceService.getSummary(
                SCHOOL_ID, SECTION_ID, DATE, DATE.plusDays(6));

        assertThat(summary.getTotalDays()).isEqualTo(2);
        assertThat(summary.getStudents()).hasSize(1);
        AttendanceSummaryResponse.StudentAttendanceStat stat = summary.getStudents().get(0);
        assertThat(stat.getPresent()).isEqualTo(1);
        assertThat(stat.getAbsent()).isEqualTo(1);
        assertThat(stat.getPercentage()).isEqualTo(50.0);
    }

    @Test
    void getByStudent_returnsHistory() {
        when(studentService.findByIdAndSchool(STUDENT_ID, SCHOOL_ID)).thenReturn(student);
        when(attendanceRepository.findBySchoolIdAndStudentIdAndDateBetween(
                SCHOOL_ID, STUDENT_ID, DATE, DATE.plusDays(30)))
                .thenReturn(List.of(record));

        List<AttendanceResponse> result = attendanceService.getByStudent(
                SCHOOL_ID, STUDENT_ID, DATE, DATE.plusDays(30));

        assertThat(result).hasSize(1);
    }

    @Test
    void update_changesStatus() {
        UpdateAttendanceRequest req = new UpdateAttendanceRequest();
        req.setStatus(AttendanceStatus.LATE);

        when(attendanceRepository.findByIdAndSchoolId("att1", SCHOOL_ID))
                .thenReturn(Optional.of(record));
        when(attendanceRepository.save(record)).thenReturn(record);

        AttendanceResponse res = attendanceService.update(SCHOOL_ID, "att1", req);

        assertThat(record.getStatus()).isEqualTo(AttendanceStatus.LATE);
        assertThat(res.getId()).isEqualTo("att1");
    }

    @Test
    void update_throwsNotFound_whenRecordMissing() {
        when(attendanceRepository.findByIdAndSchoolId("bad", SCHOOL_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> attendanceService.update(SCHOOL_ID, "bad", new UpdateAttendanceRequest()))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Attendance record not found");
    }

    @Test
    void getMyChildAttendance_returnsRecords_whenParentMatches() {
        student.setParentId("parent1");
        when(studentService.findById(STUDENT_ID)).thenReturn(student);
        when(attendanceRepository.findBySchoolIdAndStudentIdAndDateBetween(
                SCHOOL_ID, STUDENT_ID, DATE, DATE.plusDays(30)))
                .thenReturn(List.of(record));

        List<AttendanceResponse> result = attendanceService.getMyChildAttendance(
                "parent1", STUDENT_ID, DATE, DATE.plusDays(30));

        assertThat(result).hasSize(1);
    }

    @Test
    void getMyChildAttendance_throwsForbidden_whenParentMismatch() {
        student.setParentId("otherParent");
        when(studentService.findById(STUDENT_ID)).thenReturn(student);

        assertThatThrownBy(() -> attendanceService.getMyChildAttendance(
                "parent1", STUDENT_ID, DATE, DATE.plusDays(30)))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }
}
