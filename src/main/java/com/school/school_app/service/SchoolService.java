package com.school.school_app.service;

import com.school.school_app.dto.request.CreateSchoolRequest;
import com.school.school_app.dto.request.UpdateSchoolRequest;
import com.school.school_app.dto.response.SchoolResponse;
import com.school.school_app.entity.School;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;

    @Transactional
    public SchoolResponse createSchool(CreateSchoolRequest request) {
        if (request.getEmail() != null && schoolRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email already registered to a school", HttpStatus.CONFLICT);
        }
        if (request.getPhone() != null && schoolRepository.existsByPhone(request.getPhone())) {
            throw new AppException("Phone already registered to a school", HttpStatus.CONFLICT);
        }
        if (request.getAffiliationNumber() != null && schoolRepository.existsByAffiliationNumber(request.getAffiliationNumber())) {
            throw new AppException("Affiliation number already exists", HttpStatus.CONFLICT);
        }

        School school = School.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .phone(request.getPhone())
                .email(request.getEmail())
                .logoUrl(request.getLogoUrl())
                .website(request.getWebsite())
                .affiliationNumber(request.getAffiliationNumber())
                .board(request.getBoard())
                .build();

        return SchoolResponse.from(schoolRepository.save(school));
    }

    public List<SchoolResponse> getAllSchools() {
        return schoolRepository.findAll().stream()
                .map(SchoolResponse::from)
                .toList();
    }

    public List<SchoolResponse> getActiveSchools() {
        return schoolRepository.findAllByActiveTrue().stream()
                .map(SchoolResponse::from)
                .toList();
    }

    public SchoolResponse getSchool(String id) {
        return SchoolResponse.from(findById(id));
    }

    @Transactional
    public SchoolResponse updateSchool(String id, UpdateSchoolRequest request) {
        School school = findById(id);

        if (request.getName() != null) school.setName(request.getName());
        if (request.getAddress() != null) school.setAddress(request.getAddress());
        if (request.getCity() != null) school.setCity(request.getCity());
        if (request.getState() != null) school.setState(request.getState());
        if (request.getPincode() != null) school.setPincode(request.getPincode());
        if (request.getPhone() != null) school.setPhone(request.getPhone());
        if (request.getEmail() != null) school.setEmail(request.getEmail());
        if (request.getLogoUrl() != null) school.setLogoUrl(request.getLogoUrl());
        if (request.getWebsite() != null) school.setWebsite(request.getWebsite());
        if (request.getAffiliationNumber() != null) school.setAffiliationNumber(request.getAffiliationNumber());
        if (request.getBoard() != null) school.setBoard(request.getBoard());
        if (request.getActive() != null) school.setActive(request.getActive());

        return SchoolResponse.from(schoolRepository.save(school));
    }

    @Transactional
    public void deleteSchool(String id) {
        School school = findById(id);
        school.setActive(false);
        schoolRepository.save(school);
    }

    public School findById(String id) {
        return schoolRepository.findById(id)
                .orElseThrow(() -> new AppException("School not found", HttpStatus.NOT_FOUND));
    }
}
