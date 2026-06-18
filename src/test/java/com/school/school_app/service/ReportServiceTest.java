package com.school.school_app.service;

import com.school.school_app.dto.response.ReportResponse;
import com.school.school_app.entity.AttendanceStatus;
import com.school.school_app.entity.FeeStatus;
import com.school.school_app.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock StudentRepository studentRepo;
    @Mock StaffRepository staffRepo;
    @Mock FeeInvoiceRepository feeInvoiceRepo;
    @Mock ExamRepository examRepo;
    @Mock AttendanceRepository attendanceRepo;

    @InjectMocks ReportService reportService;

    private static final String SCHOOL_ID = "school1";

    @Test
    void getDashboardReport_returnsCorrectCounts() {
        when(studentRepo.countBySchoolId(SCHOOL_ID)).thenReturn(120L);
        when(staffRepo.countBySchoolId(SCHOOL_ID)).thenReturn(15L);
        when(examRepo.countBySchoolId(SCHOOL_ID)).thenReturn(8L);

        ReportResponse result = reportService.getDashboardReport(SCHOOL_ID);

        assertThat(result.getReportType()).isEqualTo("DASHBOARD");
        assertThat(result.getData().get("totalStudents")).isEqualTo(120L);
        assertThat(result.getData().get("totalStaff")).isEqualTo(15L);
        assertThat(result.getData().get("totalExams")).isEqualTo(8L);
    }

    @Test
    void getDashboardReport_hasGeneratedAt() {
        when(studentRepo.countBySchoolId(SCHOOL_ID)).thenReturn(0L);
        when(staffRepo.countBySchoolId(SCHOOL_ID)).thenReturn(0L);
        when(examRepo.countBySchoolId(SCHOOL_ID)).thenReturn(0L);

        ReportResponse result = reportService.getDashboardReport(SCHOOL_ID);

        assertThat(result.getGeneratedAt()).isNotNull();
        assertThat(result.getSchoolId()).isEqualTo(SCHOOL_ID);
    }

    @Test
    void getFeeReport_returnsStatusCounts() {
        when(feeInvoiceRepo.countBySchoolId(SCHOOL_ID)).thenReturn(50L);
        when(feeInvoiceRepo.countBySchoolIdAndStatus(SCHOOL_ID, FeeStatus.PAID)).thenReturn(30L);
        when(feeInvoiceRepo.countBySchoolIdAndStatus(SCHOOL_ID, FeeStatus.PENDING)).thenReturn(15L);
        when(feeInvoiceRepo.countBySchoolIdAndStatus(SCHOOL_ID, FeeStatus.OVERDUE)).thenReturn(5L);

        ReportResponse result = reportService.getFeeReport(SCHOOL_ID);

        assertThat(result.getReportType()).isEqualTo("FEE_SUMMARY");
        assertThat(result.getData().get("totalInvoices")).isEqualTo(50L);
        assertThat(result.getData().get("paidInvoices")).isEqualTo(30L);
    }

    @Test
    void getAttendanceReport_returnsPresenceData() {
        when(attendanceRepo.countBySchoolIdAndDateAndStatus(eq(SCHOOL_ID), any(), eq(AttendanceStatus.PRESENT)))
                .thenReturn(80L);
        when(attendanceRepo.countBySchoolIdAndDateAndStatus(eq(SCHOOL_ID), any(), eq(AttendanceStatus.ABSENT)))
                .thenReturn(10L);

        ReportResponse result = reportService.getAttendanceReport(SCHOOL_ID);

        assertThat(result.getReportType()).isEqualTo("ATTENDANCE_SUMMARY");
        assertThat(result.getData().get("presentToday")).isEqualTo(80L);
        assertThat(result.getData().get("absentToday")).isEqualTo(10L);
    }

    @Test
    void getExamReport_returnsStatusBreakdown() {
        when(examRepo.countBySchoolId(SCHOOL_ID)).thenReturn(20L);
        when(examRepo.countBySchoolIdAndStatus(SCHOOL_ID, "UPCOMING")).thenReturn(5L);
        when(examRepo.countBySchoolIdAndStatus(SCHOOL_ID, "COMPLETED")).thenReturn(12L);
        when(examRepo.countBySchoolIdAndStatus(SCHOOL_ID, "ONGOING")).thenReturn(3L);

        ReportResponse result = reportService.getExamReport(SCHOOL_ID);

        assertThat(result.getReportType()).isEqualTo("EXAM_SUMMARY");
        assertThat(result.getData().get("totalExams")).isEqualTo(20L);
        assertThat(result.getData().get("upcomingExams")).isEqualTo(5L);
    }

    @Test
    void getFeeReport_hasSchoolId() {
        when(feeInvoiceRepo.countBySchoolId(SCHOOL_ID)).thenReturn(0L);
        when(feeInvoiceRepo.countBySchoolIdAndStatus(any(), any())).thenReturn(0L);

        ReportResponse result = reportService.getFeeReport(SCHOOL_ID);

        assertThat(result.getSchoolId()).isEqualTo(SCHOOL_ID);
    }

    @Test
    void getAttendanceReport_hasDateField() {
        when(attendanceRepo.countBySchoolIdAndDateAndStatus(eq(SCHOOL_ID), any(), any()))
                .thenReturn(0L);

        ReportResponse result = reportService.getAttendanceReport(SCHOOL_ID);

        assertThat(result.getData()).containsKey("date");
        assertThat(result.getData().get("date")).isEqualTo(LocalDate.now().toString());
    }

    @Test
    void getDashboardReport_zeroCountsWhenNoData() {
        when(studentRepo.countBySchoolId(SCHOOL_ID)).thenReturn(0L);
        when(staffRepo.countBySchoolId(SCHOOL_ID)).thenReturn(0L);
        when(examRepo.countBySchoolId(SCHOOL_ID)).thenReturn(0L);

        ReportResponse result = reportService.getDashboardReport(SCHOOL_ID);

        assertThat(result.getData().get("totalStudents")).isEqualTo(0L);
        assertThat(result.getData().get("totalStaff")).isEqualTo(0L);
    }
}
