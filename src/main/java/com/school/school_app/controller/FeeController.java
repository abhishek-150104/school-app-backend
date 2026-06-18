package com.school.school_app.controller;

import com.school.school_app.dto.request.CreateFeeInvoiceRequest;
import com.school.school_app.dto.request.CreateFeeStructureRequest;
import com.school.school_app.dto.request.RecordPaymentRequest;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.dto.response.FeeInvoiceResponse;
import com.school.school_app.dto.response.FeePaymentResponse;
import com.school.school_app.dto.response.FeeStructureResponse;
import com.school.school_app.entity.User;
import com.school.school_app.service.FeeService;
import com.school.school_app.service.SchoolContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FeeController {

    private final FeeService feeService;
    private final SchoolContextService schoolContextService;

    @PostMapping("/api/fees/structures")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<FeeStructureResponse>> createStructure(
            @RequestBody CreateFeeStructureRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Fee structure created",
                feeService.createStructure(schoolContextService.getSchoolId(),
                        schoolContextService.getSchoolName(), request,
                        currentUser.getId(), currentUser.getFullName())));
    }

    @GetMapping("/api/fees/structures")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<List<FeeStructureResponse>>> getStructures() {
        return ResponseEntity.ok(ApiResponse.success(
                feeService.getStructures(schoolContextService.getSchoolId())));
    }

    @PostMapping("/api/fees/invoices")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<FeeInvoiceResponse>> createInvoice(
            @RequestBody CreateFeeInvoiceRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Fee invoice created",
                feeService.createInvoice(schoolContextService.getSchoolId(),
                        schoolContextService.getSchoolName(), request,
                        currentUser.getId(), currentUser.getFullName())));
    }

    @GetMapping("/api/fees/invoices")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<List<FeeInvoiceResponse>>> getInvoices() {
        return ResponseEntity.ok(ApiResponse.success(
                feeService.getInvoices(schoolContextService.getSchoolId())));
    }

    @GetMapping("/api/fees/invoices/student/{studentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<FeeInvoiceResponse>>> getStudentInvoices(
            @PathVariable String studentId) {
        return ResponseEntity.ok(ApiResponse.success(
                feeService.getStudentInvoices(schoolContextService.getSchoolId(), studentId)));
    }

    @PostMapping("/api/fees/payments")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<FeePaymentResponse>> recordPayment(
            @RequestBody RecordPaymentRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Payment recorded",
                feeService.recordPayment(schoolContextService.getSchoolId(), request,
                        currentUser.getId(), currentUser.getFullName())));
    }

    @GetMapping("/api/fees/invoices/{invoiceId}/payments")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<List<FeePaymentResponse>>> getInvoicePayments(
            @PathVariable String invoiceId) {
        return ResponseEntity.ok(ApiResponse.success(
                feeService.getInvoicePayments(schoolContextService.getSchoolId(), invoiceId)));
    }
}
