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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeeService {

    private final FeeStructureRepository feeStructureRepo;
    private final FeeInvoiceRepository feeInvoiceRepo;
    private final FeePaymentRepository feePaymentRepo;
    private final StudentRepository studentRepo;
    private final AcademicYearRepository academicYearRepo;
    private final ClassRoomRepository classRoomRepo;

    public FeeStructureResponse createStructure(String schoolId, String schoolName,
                                                CreateFeeStructureRequest req,
                                                String creatorId, String creatorName) {
        if (feeStructureRepo.existsBySchoolIdAndAcademicYearIdAndClassRoomId(
                schoolId, req.getAcademicYearId(), req.getClassRoomId())) {
            throw new AppException("Fee structure already exists for this class and year", HttpStatus.CONFLICT);
        }
        AcademicYear ay = academicYearRepo.findById(req.getAcademicYearId())
                .orElseThrow(() -> new AppException("Academic year not found", HttpStatus.NOT_FOUND));
        ClassRoom cr = classRoomRepo.findById(req.getClassRoomId())
                .orElseThrow(() -> new AppException("Class room not found", HttpStatus.NOT_FOUND));

        BigDecimal total = sum(req.getTuitionFee(), req.getExamFee(),
                req.getLibraryFee(), req.getSportsFee(), req.getMiscFee());

        FeeStructure fs = FeeStructure.builder()
                .schoolId(schoolId).schoolName(schoolName)
                .academicYearId(ay.getId()).academicYearName(ay.getLabel())
                .classRoomId(cr.getId()).classRoomName(cr.getName())
                .tuitionFee(req.getTuitionFee()).examFee(req.getExamFee())
                .libraryFee(req.getLibraryFee()).sportsFee(req.getSportsFee())
                .miscFee(req.getMiscFee()).totalFee(total)
                .createdById(creatorId).createdByName(creatorName)
                .build();
        return FeeStructureResponse.from(feeStructureRepo.save(fs));
    }

    public List<FeeStructureResponse> getStructures(String schoolId) {
        return feeStructureRepo.findBySchoolId(schoolId)
                .stream().map(FeeStructureResponse::from).toList();
    }

    public FeeInvoiceResponse createInvoice(String schoolId, String schoolName,
                                             CreateFeeInvoiceRequest req,
                                             String creatorId, String creatorName) {
        Student student = studentRepo.findById(req.getStudentId())
                .filter(s -> s.getSchoolId().equals(schoolId))
                .orElseThrow(() -> new AppException("Student not found", HttpStatus.NOT_FOUND));
        FeeStructure fs = feeStructureRepo.findById(req.getFeeStructureId())
                .orElseThrow(() -> new AppException("Fee structure not found", HttpStatus.NOT_FOUND));

        FeeInvoice inv = FeeInvoice.builder()
                .schoolId(schoolId).schoolName(schoolName)
                .studentId(student.getId()).studentFullName(student.getFullName())
                .admissionNumber(student.getAdmissionNumber())
                .classRoomId(student.getClassRoomId()).classRoomName(student.getClassRoomName())
                .sectionId(student.getSectionId()).sectionName(student.getSectionName())
                .academicYearId(fs.getAcademicYearId()).academicYearName(fs.getAcademicYearName())
                .feeStructureId(fs.getId())
                .totalAmount(fs.getTotalFee()).paidAmount(BigDecimal.ZERO)
                .dueAmount(fs.getTotalFee()).status(FeeStatus.PENDING)
                .dueDate(req.getDueDate())
                .createdById(creatorId).createdByName(creatorName)
                .build();
        return FeeInvoiceResponse.from(feeInvoiceRepo.save(inv));
    }

    public List<FeeInvoiceResponse> getInvoices(String schoolId) {
        return feeInvoiceRepo.findBySchoolId(schoolId)
                .stream().map(FeeInvoiceResponse::from).toList();
    }

    public List<FeeInvoiceResponse> getStudentInvoices(String schoolId, String studentId) {
        return feeInvoiceRepo.findBySchoolIdAndStudentId(schoolId, studentId)
                .stream().map(FeeInvoiceResponse::from).toList();
    }

    public FeePaymentResponse recordPayment(String schoolId, RecordPaymentRequest req,
                                             String collectorId, String collectorName) {
        FeeInvoice inv = feeInvoiceRepo.findById(req.getInvoiceId())
                .filter(i -> i.getSchoolId().equals(schoolId))
                .orElseThrow(() -> new AppException("Invoice not found", HttpStatus.NOT_FOUND));

        if (inv.getStatus() == FeeStatus.PAID) {
            throw new AppException("Invoice already fully paid", HttpStatus.BAD_REQUEST);
        }

        FeePayment payment = FeePayment.builder()
                .schoolId(schoolId)
                .invoiceId(inv.getId())
                .studentId(inv.getStudentId()).studentFullName(inv.getStudentFullName())
                .amount(req.getAmount())
                .paymentMode(req.getPaymentMode())
                .transactionId(req.getTransactionId())
                .remarks(req.getRemarks())
                .collectedById(collectorId).collectedByName(collectorName)
                .build();
        payment = feePaymentRepo.save(payment);

        BigDecimal newPaid = inv.getPaidAmount().add(req.getAmount());
        BigDecimal newDue = inv.getTotalAmount().subtract(newPaid);
        FeeStatus newStatus = newDue.compareTo(BigDecimal.ZERO) <= 0
                ? FeeStatus.PAID
                : newPaid.compareTo(BigDecimal.ZERO) > 0 ? FeeStatus.PARTIAL : FeeStatus.PENDING;

        inv.setPaidAmount(newPaid);
        inv.setDueAmount(newDue.max(BigDecimal.ZERO));
        inv.setStatus(newStatus);
        feeInvoiceRepo.save(inv);

        return FeePaymentResponse.from(payment);
    }

    public List<FeePaymentResponse> getInvoicePayments(String schoolId, String invoiceId) {
        return feePaymentRepo.findBySchoolIdAndInvoiceId(schoolId, invoiceId)
                .stream().map(FeePaymentResponse::from).toList();
    }

    private BigDecimal sum(BigDecimal... values) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal v : values) {
            if (v != null) total = total.add(v);
        }
        return total;
    }
}
