package com.school.school_app.service;

import com.school.school_app.dto.request.CreateAcademicYearRequest;
import com.school.school_app.dto.response.AcademicYearResponse;
import com.school.school_app.entity.AcademicYear;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.AcademicYearRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicYearService {

    private final AcademicYearRepository academicYearRepository;
    private final SchoolService schoolService;

    @Transactional
    public AcademicYearResponse create(String schoolId, CreateAcademicYearRequest request) {
        schoolService.findById(schoolId);

        if (academicYearRepository.existsBySchoolIdAndLabel(schoolId, request.getLabel())) {
            throw new AppException("Academic year '" + request.getLabel() + "' already exists for this school", HttpStatus.CONFLICT);
        }
        if (request.getEndYear() <= request.getStartYear()) {
            throw new AppException("End year must be greater than start year", HttpStatus.BAD_REQUEST);
        }

        if (request.isActive()) {
            deactivateAll(schoolId);
        }

        AcademicYear year = AcademicYear.builder()
                .schoolId(schoolId)
                .label(request.getLabel())
                .startYear(request.getStartYear())
                .endYear(request.getEndYear())
                .active(request.isActive())
                .build();

        return AcademicYearResponse.from(academicYearRepository.save(year));
    }

    public List<AcademicYearResponse> getAllBySchool(String schoolId) {
        schoolService.findById(schoolId);
        return academicYearRepository.findBySchoolIdOrderByStartYearDesc(schoolId).stream()
                .map(AcademicYearResponse::from)
                .toList();
    }

    public AcademicYearResponse getActive(String schoolId) {
        schoolService.findById(schoolId);
        AcademicYear year = academicYearRepository.findBySchoolIdAndActiveTrue(schoolId)
                .orElseThrow(() -> new AppException("No active academic year found for this school", HttpStatus.NOT_FOUND));
        return AcademicYearResponse.from(year);
    }

    @Transactional
    public AcademicYearResponse activate(String schoolId, String yearId) {
        schoolService.findById(schoolId);
        AcademicYear year = findByIdAndSchool(yearId, schoolId);

        deactivateAll(schoolId);
        year.setActive(true);

        return AcademicYearResponse.from(academicYearRepository.save(year));
    }

    @Transactional
    public void delete(String schoolId, String yearId) {
        schoolService.findById(schoolId);
        AcademicYear year = findByIdAndSchool(yearId, schoolId);
        academicYearRepository.delete(year);
    }

    public AcademicYear findByIdAndSchool(String id, String schoolId) {
        return academicYearRepository.findById(id)
                .filter(y -> y.getSchoolId().equals(schoolId))
                .orElseThrow(() -> new AppException("Academic year not found", HttpStatus.NOT_FOUND));
    }

    private void deactivateAll(String schoolId) {
        List<AcademicYear> years = academicYearRepository.findBySchoolId(schoolId);
        years.forEach(y -> y.setActive(false));
        academicYearRepository.saveAll(years);
    }
}
