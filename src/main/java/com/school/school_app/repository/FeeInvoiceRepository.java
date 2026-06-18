package com.school.school_app.repository;

import com.school.school_app.entity.FeeInvoice;
import com.school.school_app.entity.FeeStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FeeInvoiceRepository extends MongoRepository<FeeInvoice, String> {
    List<FeeInvoice> findBySchoolId(String schoolId);
    List<FeeInvoice> findBySchoolIdAndStudentId(String schoolId, String studentId);
    List<FeeInvoice> findBySchoolIdAndStatus(String schoolId, FeeStatus status);
    List<FeeInvoice> findBySchoolIdAndClassRoomId(String schoolId, String classRoomId);
    List<FeeInvoice> findBySchoolIdAndAcademicYearId(String schoolId, String academicYearId);

    long countBySchoolId(String schoolId);
    long countBySchoolIdAndStatus(String schoolId, FeeStatus status);
}
