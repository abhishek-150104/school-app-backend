package com.school.school_app.repository;

import com.school.school_app.entity.FeePayment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FeePaymentRepository extends MongoRepository<FeePayment, String> {
    List<FeePayment> findBySchoolIdAndInvoiceId(String schoolId, String invoiceId);
    List<FeePayment> findBySchoolIdAndStudentId(String schoolId, String studentId);
}
