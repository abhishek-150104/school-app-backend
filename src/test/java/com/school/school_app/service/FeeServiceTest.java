package com.school.school_app.service;

import com.school.school_app.dto.request.CreateFeeInvoiceRequest;
import com.school.school_app.dto.request.CreateFeeStructureRequest;
import com.school.school_app.dto.request.RecordPaymentRequest;
import com.school.school_app.dto.response.FeeInvoiceResponse;
import com.school.school_app.dto.response.FeePaymentResponse;
import com.school.school_app.dto.response.FeeStructureResponse;
import com.school.school_app.entity.*;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeeServiceTest {

    @Mock FeeStructureRepository feeStructureRepo;
    @Mock FeeInvoiceRepository feeInvoiceRepo;
    @Mock FeePaymentRepository feePaymentRepo;
    @Mock StudentRepository studentRepo;
    @Mock AcademicYearRepository academicYearRepo;
    @Mock ClassRoomRepository classRoomRepo;

    @InjectMocks FeeService feeService;

    private static final String SCHOOL_ID = "school1";
    private static final String SCHOOL_NAME = "Test School";

    @Test
    void createStructure_success() {
        CreateFeeStructureRequest req = new CreateFeeStructureRequest();
        req.setAcademicYearId("ay1");
        req.setClassRoomId("cr1");
        req.setTuitionFee(new BigDecimal("5000"));
        req.setExamFee(new BigDecimal("500"));
        req.setLibraryFee(new BigDecimal("200"));
        req.setSportsFee(new BigDecimal("300"));
        req.setMiscFee(new BigDecimal("100"));

        AcademicYear ay = AcademicYear.builder().id("ay1").label("2024-25").build();
        ClassRoom cr = ClassRoom.builder().id("cr1").name("Class 1").build();
        FeeStructure saved = FeeStructure.builder().id("fs1").schoolId(SCHOOL_ID)
                .academicYearId("ay1").academicYearName("2024-25")
                .classRoomId("cr1").classRoomName("Class 1")
                .totalFee(new BigDecimal("6100")).build();

        when(feeStructureRepo.existsBySchoolIdAndAcademicYearIdAndClassRoomId(any(), any(), any())).thenReturn(false);
        when(academicYearRepo.findById("ay1")).thenReturn(Optional.of(ay));
        when(classRoomRepo.findById("cr1")).thenReturn(Optional.of(cr));
        when(feeStructureRepo.save(any())).thenReturn(saved);

        FeeStructureResponse result = feeService.createStructure(SCHOOL_ID, SCHOOL_NAME, req, "u1", "Admin");

        assertThat(result.getId()).isEqualTo("fs1");
        assertThat(result.getClassRoomName()).isEqualTo("Class 1");
    }

    @Test
    void createStructure_duplicateThrows() {
        CreateFeeStructureRequest req = new CreateFeeStructureRequest();
        req.setAcademicYearId("ay1");
        req.setClassRoomId("cr1");

        when(feeStructureRepo.existsBySchoolIdAndAcademicYearIdAndClassRoomId(any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> feeService.createStructure(SCHOOL_ID, SCHOOL_NAME, req, "u1", "Admin"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void getStructures_returnsList() {
        FeeStructure fs = FeeStructure.builder().id("fs1").schoolId(SCHOOL_ID).totalFee(BigDecimal.TEN).build();
        when(feeStructureRepo.findBySchoolId(SCHOOL_ID)).thenReturn(List.of(fs));

        List<FeeStructureResponse> result = feeService.getStructures(SCHOOL_ID);

        assertThat(result).hasSize(1);
    }

    @Test
    void createInvoice_success() {
        CreateFeeInvoiceRequest req = new CreateFeeInvoiceRequest();
        req.setStudentId("s1");
        req.setFeeStructureId("fs1");
        req.setDueDate(LocalDate.now().plusDays(30));

        Student student = Student.builder().id("s1").schoolId(SCHOOL_ID)
                .fullName("John Doe").admissionNumber("ADM001")
                .classRoomId("cr1").classRoomName("Class 1")
                .sectionId("sec1").sectionName("A").build();
        FeeStructure fs = FeeStructure.builder().id("fs1").schoolId(SCHOOL_ID)
                .academicYearId("ay1").academicYearName("2024-25")
                .totalFee(new BigDecimal("6100")).build();
        FeeInvoice saved = FeeInvoice.builder().id("inv1").studentId("s1")
                .totalAmount(new BigDecimal("6100")).paidAmount(BigDecimal.ZERO)
                .dueAmount(new BigDecimal("6100")).status(FeeStatus.PENDING).build();

        when(studentRepo.findById("s1")).thenReturn(Optional.of(student));
        when(feeStructureRepo.findById("fs1")).thenReturn(Optional.of(fs));
        when(feeInvoiceRepo.save(any())).thenReturn(saved);

        FeeInvoiceResponse result = feeService.createInvoice(SCHOOL_ID, SCHOOL_NAME, req, "u1", "Admin");

        assertThat(result.getId()).isEqualTo("inv1");
        assertThat(result.getStatus()).isEqualTo(FeeStatus.PENDING);
    }

    @Test
    void createInvoice_studentNotFoundThrows() {
        CreateFeeInvoiceRequest req = new CreateFeeInvoiceRequest();
        req.setStudentId("s1");
        req.setFeeStructureId("fs1");

        when(studentRepo.findById("s1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> feeService.createInvoice(SCHOOL_ID, SCHOOL_NAME, req, "u1", "Admin"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    void recordPayment_marksInvoicePaid() {
        RecordPaymentRequest req = new RecordPaymentRequest();
        req.setInvoiceId("inv1");
        req.setAmount(new BigDecimal("6100"));
        req.setPaymentMode("CASH");

        FeeInvoice inv = FeeInvoice.builder().id("inv1").schoolId(SCHOOL_ID)
                .studentId("s1").studentFullName("John")
                .totalAmount(new BigDecimal("6100")).paidAmount(BigDecimal.ZERO)
                .dueAmount(new BigDecimal("6100")).status(FeeStatus.PENDING).build();
        FeePayment savedPayment = FeePayment.builder().id("pay1").amount(new BigDecimal("6100")).build();

        when(feeInvoiceRepo.findById("inv1")).thenReturn(Optional.of(inv));
        when(feePaymentRepo.save(any())).thenReturn(savedPayment);
        when(feeInvoiceRepo.save(any())).thenReturn(inv);

        FeePaymentResponse result = feeService.recordPayment(SCHOOL_ID, req, "u1", "Admin");

        assertThat(result.getId()).isEqualTo("pay1");
        verify(feeInvoiceRepo).save(argThat(i -> i.getStatus() == FeeStatus.PAID));
    }

    @Test
    void recordPayment_alreadyPaidThrows() {
        RecordPaymentRequest req = new RecordPaymentRequest();
        req.setInvoiceId("inv1");
        req.setAmount(BigDecimal.TEN);

        FeeInvoice inv = FeeInvoice.builder().id("inv1").schoolId(SCHOOL_ID)
                .status(FeeStatus.PAID).build();
        when(feeInvoiceRepo.findById("inv1")).thenReturn(Optional.of(inv));

        assertThatThrownBy(() -> feeService.recordPayment(SCHOOL_ID, req, "u1", "Admin"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("already fully paid");
    }

    @Test
    void getInvoicePayments_returnsPayments() {
        FeePayment pay = FeePayment.builder().id("pay1").invoiceId("inv1").amount(BigDecimal.TEN).build();
        when(feePaymentRepo.findBySchoolIdAndInvoiceId(SCHOOL_ID, "inv1")).thenReturn(List.of(pay));

        List<FeePaymentResponse> result = feeService.getInvoicePayments(SCHOOL_ID, "inv1");

        assertThat(result).hasSize(1);
    }
}
