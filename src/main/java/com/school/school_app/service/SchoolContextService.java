package com.school.school_app.service;

import com.school.school_app.exception.AppException;
import com.school.school_app.repository.SchoolRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchoolContextService {

    private final SchoolRepository schoolRepository;

    private String cachedSchoolId;
    private String cachedSchoolName;

    @PostConstruct
    public void init() {
        schoolRepository.findAll().stream().findFirst().ifPresent(school -> {
            cachedSchoolId = school.getId();
            cachedSchoolName = school.getName();
        });
    }

    public String getSchoolId() {
        if (cachedSchoolId == null) {
            throw new AppException("School not configured. Please seed the school record.", HttpStatus.SERVICE_UNAVAILABLE);
        }
        return cachedSchoolId;
    }

    public String getSchoolName() {
        return cachedSchoolName;
    }

    public void refresh() {
        init();
    }
}
