package com.school.school_app.service;

import com.school.school_app.dto.response.ReportResponse;
import com.school.school_app.entity.AttendanceStatus;
import com.school.school_app.entity.FeeStatus;
import com.school.school_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final StudentRepository studentRepo;
    private final StaffRepository staffRepo;
    private final FeeInvoiceRepository feeInvoiceRepo;
    private final ExamRepository examRepo;
    private final AttendanceRepository attendanceRepo;

    public ReportResponse getDashboardReport(String schoolId) {
        Map<String, Object> data = new HashMap<>();
        data.put("totalStudents", studentRepo.countBySchoolId(schoolId));
        data.put("totalStaff", staffRepo.countBySchoolId(schoolId));
        data.put("totalExams", examRepo.countBySchoolId(schoolId));

        return ReportResponse.builder()
                .schoolId(schoolId)
                .reportType("DASHBOARD")
                .data(data)
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }

    public ReportResponse getFeeReport(String schoolId) {
        Map<String, Object> data = new HashMap<>();
        data.put("totalInvoices", feeInvoiceRepo.countBySchoolId(schoolId));
        data.put("paidInvoices", feeInvoiceRepo.countBySchoolIdAndStatus(schoolId, FeeStatus.PAID));
        data.put("pendingInvoices", feeInvoiceRepo.countBySchoolIdAndStatus(schoolId, FeeStatus.PENDING));
        data.put("overdueInvoices", feeInvoiceRepo.countBySchoolIdAndStatus(schoolId, FeeStatus.OVERDUE));

        return ReportResponse.builder()
                .schoolId(schoolId)
                .reportType("FEE_SUMMARY")
                .data(data)
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }

    public ReportResponse getAttendanceReport(String schoolId) {
        Map<String, Object> data = new HashMap<>();
        LocalDate today = LocalDate.now();
        long presentToday = attendanceRepo.countBySchoolIdAndDateAndStatus(
                schoolId, today, AttendanceStatus.PRESENT);
        long absentToday = attendanceRepo.countBySchoolIdAndDateAndStatus(
                schoolId, today, AttendanceStatus.ABSENT);
        data.put("presentToday", presentToday);
        data.put("absentToday", absentToday);
        data.put("date", today.toString());

        return ReportResponse.builder()
                .schoolId(schoolId)
                .reportType("ATTENDANCE_SUMMARY")
                .data(data)
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }

    public ReportResponse getExamReport(String schoolId) {
        Map<String, Object> data = new HashMap<>();
        data.put("totalExams", examRepo.countBySchoolId(schoolId));
        data.put("upcomingExams", examRepo.countBySchoolIdAndStatus(schoolId, "UPCOMING"));
        data.put("completedExams", examRepo.countBySchoolIdAndStatus(schoolId, "COMPLETED"));
        data.put("ongoingExams", examRepo.countBySchoolIdAndStatus(schoolId, "ONGOING"));

        return ReportResponse.builder()
                .schoolId(schoolId)
                .reportType("EXAM_SUMMARY")
                .data(data)
                .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }
}
