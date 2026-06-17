package com.school.school_app.service;

import com.school.school_app.dto.request.BulkMarkAttendanceRequest;
import com.school.school_app.dto.request.UpdateAttendanceRequest;
import com.school.school_app.dto.response.AttendanceResponse;
import com.school.school_app.dto.response.AttendanceSummaryResponse;
import com.school.school_app.entity.*;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final SectionService sectionService;
    private final ClassRoomService classRoomService;
    private final StudentService studentService;

    @Transactional
    public List<AttendanceResponse> markBulk(
            String schoolId,
            String sectionId,
            BulkMarkAttendanceRequest request,
            String markedById,
            String markedByName) {

        Section section = sectionService.findById(sectionId);
        classRoomService.findByIdAndSchool(section.getClassRoomId(), schoolId);

        for (BulkMarkAttendanceRequest.AttendanceEntry entry : request.getRecords()) {
            Student student = studentService.findByIdAndSchool(entry.getStudentId(), schoolId);

            AttendanceRecord record = attendanceRepository
                    .findBySchoolIdAndSectionIdAndDateAndStudentId(
                            schoolId, sectionId, request.getDate(), entry.getStudentId())
                    .orElse(AttendanceRecord.builder()
                            .schoolId(schoolId)
                            .classRoomId(section.getClassRoomId())
                            .classRoomName(section.getClassRoomName())
                            .sectionId(sectionId)
                            .sectionName(section.getName())
                            .date(request.getDate())
                            .studentId(student.getId())
                            .studentFullName(student.getFullName())
                            .admissionNumber(student.getAdmissionNumber())
                            .build());

            record.setStatus(entry.getStatus());
            record.setMarkedById(markedById);
            record.setMarkedByName(markedByName);
            if (entry.getRemarks() != null) record.setRemarks(entry.getRemarks());

            attendanceRepository.save(record);
        }

        return attendanceRepository
                .findBySchoolIdAndSectionIdAndDate(schoolId, sectionId, request.getDate())
                .stream().map(AttendanceResponse::from).toList();
    }

    public List<AttendanceResponse> getBySection(
            String schoolId, String sectionId, LocalDate date) {
        Section section = sectionService.findById(sectionId);
        classRoomService.findByIdAndSchool(section.getClassRoomId(), schoolId);
        return attendanceRepository
                .findBySchoolIdAndSectionIdAndDate(schoolId, sectionId, date)
                .stream().map(AttendanceResponse::from).toList();
    }

    public AttendanceSummaryResponse getSummary(
            String schoolId, String sectionId, LocalDate from, LocalDate to) {

        Section section = sectionService.findById(sectionId);
        classRoomService.findByIdAndSchool(section.getClassRoomId(), schoolId);

        List<AttendanceRecord> records = attendanceRepository
                .findBySchoolIdAndSectionIdAndDateBetween(schoolId, sectionId, from, to);

        int totalDays = (int) records.stream()
                .map(AttendanceRecord::getDate).distinct().count();

        Map<String, List<AttendanceRecord>> byStudent = records.stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getStudentId));

        List<AttendanceSummaryResponse.StudentAttendanceStat> stats = byStudent.entrySet().stream()
                .map(e -> {
                    List<AttendanceRecord> sr = e.getValue();
                    AttendanceRecord first = sr.get(0);
                    int present = count(sr, AttendanceStatus.PRESENT);
                    int absent  = count(sr, AttendanceStatus.ABSENT);
                    int late    = count(sr, AttendanceStatus.LATE);
                    int excused = count(sr, AttendanceStatus.EXCUSED);
                    double total = present + absent + late + excused;
                    double pct = total > 0
                            ? Math.round(((present + late) * 100.0 / total) * 10.0) / 10.0
                            : 0.0;
                    return AttendanceSummaryResponse.StudentAttendanceStat.builder()
                            .studentId(e.getKey())
                            .fullName(first.getStudentFullName())
                            .admissionNumber(first.getAdmissionNumber())
                            .present(present).absent(absent)
                            .late(late).excused(excused)
                            .percentage(pct)
                            .build();
                })
                .sorted(Comparator.comparing(AttendanceSummaryResponse.StudentAttendanceStat::getFullName))
                .toList();

        return AttendanceSummaryResponse.builder()
                .sectionId(sectionId)
                .sectionName(section.getName())
                .classRoomName(section.getClassRoomName())
                .from(from).to(to)
                .totalDays(totalDays)
                .students(stats)
                .build();
    }

    public List<AttendanceResponse> getByStudent(
            String schoolId, String studentId, LocalDate from, LocalDate to) {
        studentService.findByIdAndSchool(studentId, schoolId);
        return attendanceRepository
                .findBySchoolIdAndStudentIdAndDateBetween(schoolId, studentId, from, to)
                .stream()
                .sorted(Comparator.comparing(AttendanceRecord::getDate).reversed())
                .map(AttendanceResponse::from)
                .toList();
    }

    @Transactional
    public AttendanceResponse update(
            String schoolId, String attendanceId, UpdateAttendanceRequest request) {
        AttendanceRecord record = attendanceRepository.findByIdAndSchoolId(attendanceId, schoolId)
                .orElseThrow(() -> new AppException("Attendance record not found", HttpStatus.NOT_FOUND));

        if (request.getStatus() != null) record.setStatus(request.getStatus());
        if (request.getRemarks() != null) record.setRemarks(request.getRemarks());

        return AttendanceResponse.from(attendanceRepository.save(record));
    }

    public List<AttendanceResponse> getMyChildAttendance(
            String parentId, String studentId, LocalDate from, LocalDate to) {
        Student student = studentService.findById(studentId);

        if (!parentId.equals(student.getParentId())) {
            throw new AppException("You are not authorized to view this student's attendance",
                    HttpStatus.FORBIDDEN);
        }

        return attendanceRepository
                .findBySchoolIdAndStudentIdAndDateBetween(student.getSchoolId(), studentId, from, to)
                .stream()
                .sorted(Comparator.comparing(AttendanceRecord::getDate).reversed())
                .map(AttendanceResponse::from)
                .toList();
    }

    private int count(List<AttendanceRecord> records, AttendanceStatus status) {
        return (int) records.stream().filter(r -> r.getStatus() == status).count();
    }
}
