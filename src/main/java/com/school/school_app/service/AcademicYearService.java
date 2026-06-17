package com.school.school_app.service;

import com.school.school_app.dto.request.CreateAcademicYearRequest;
import com.school.school_app.dto.response.AcademicYearResponse;
import com.school.school_app.entity.AcademicYear;
import com.school.school_app.entity.School;
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
    public AcademicYearResponse create(Long schoolId, CreateAcademicYearRequest request) {
        School school = schoolService.findById(schoolId);

        if (academicYearRepository.existsBySchoolIdAndLabel(schoolId, request.getLabel())) {
            throw new AppException("Academic year '" + request.getLabel() + "' already exists for this school", HttpStatus.CONFLICT);
        }
        if (request.getEndYear() <= request.getStartYear()) {
            throw new AppException("End year must be greater than start year", HttpStatus.BAD_REQUEST);
        }

        if (request.isActive()) {
            academicYearRepository.deactivateAllBySchoolId(schoolId);
        }

        AcademicYear year = AcademicYear.builder()
                .school(school)
                .label(request.getLabel())
                .startYear(request.getStartYear())
                .endYear(request.getEndYear())
                .active(request.isActive())
                .build();

        return AcademicYearResponse.from(academicYearRepository.save(year));
    }

    public List<AcademicYearResponse> getAllBySchool(Long schoolId) {
        schoolService.findById(schoolId);
        return academicYearRepository.findBySchoolIdOrderByStartYearDesc(schoolId).stream()
                .map(AcademicYearResponse::from)
                .toList();
    }

    public AcademicYearResponse getActive(Long schoolId) {
        schoolService.findById(schoolId);
        AcademicYear year = academicYearRepository.findBySchoolIdAndActiveTrue(schoolId)
                .orElseThrow(() -> new AppException("No active academic year found for this school", HttpStatus.NOT_FOUND));
        return AcademicYearResponse.from(year);
    }

    @Transactional
    public AcademicYearResponse activate(Long schoolId, Long yearId) {
        schoolService.findById(schoolId);
        AcademicYear year = findByIdAndSchool(yearId, schoolId);

        academicYearRepository.deactivateAllBySchoolId(schoolId);
        year.setActive(true);

        return AcademicYearResponse.from(academicYearRepository.save(year));
    }

    @Transactional
    public void delete(Long schoolId, Long yearId) {
        schoolService.findById(schoolId);
        AcademicYear year = findByIdAndSchool(yearId, schoolId);
        academicYearRepository.delete(year);
    }

    public AcademicYear findByIdAndSchool(Long id, Long schoolId) {
        return academicYearRepository.findById(id)
                .filter(y -> y.getSchool().getId().equals(schoolId))
                .orElseThrow(() -> new AppException("Academic year not found", HttpStatus.NOT_FOUND));
    }
}
