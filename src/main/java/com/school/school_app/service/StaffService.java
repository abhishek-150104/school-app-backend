package com.school.school_app.service;

import com.school.school_app.dto.request.CreateStaffRequest;
import com.school.school_app.dto.request.UpdateStaffRequest;
import com.school.school_app.dto.response.SectionResponse;
import com.school.school_app.dto.response.StaffResponse;
import com.school.school_app.dto.response.TeacherProfileResponse;
import com.school.school_app.entity.*;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.SectionRepository;
import com.school.school_app.repository.StaffRepository;
import com.school.school_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final SchoolService schoolService;
    private final UserRepository userRepository;
    private final SectionRepository sectionRepository;

    @Transactional
    public StaffResponse create(String schoolId, CreateStaffRequest request) {
        School school = schoolService.findById(schoolId);

        if (staffRepository.existsBySchoolIdAndEmployeeId(schoolId, request.getEmployeeId())) {
            throw new AppException(
                    "Employee ID '" + request.getEmployeeId() + "' already exists in this school",
                    HttpStatus.CONFLICT);
        }

        if (staffRepository.existsByUserId(request.getUserId())) {
            throw new AppException("This user already has a staff profile", HttpStatus.CONFLICT);
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        if (user.getRole() != Role.TEACHER) {
            throw new AppException("User must have TEACHER role to be added as staff", HttpStatus.BAD_REQUEST);
        }

        String fullName = request.getFirstName().trim() + " " + request.getLastName().trim();

        Staff staff = Staff.builder()
                .schoolId(school.getId())
                .schoolName(school.getName())
                .userId(user.getId())
                .employeeId(request.getEmployeeId())
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .fullName(fullName)
                .designation(request.getDesignation())
                .subjects(request.getSubjects())
                .qualification(request.getQualification())
                .joiningDate(request.getJoiningDate())
                .address(request.getAddress())
                .active(true)
                .build();

        return StaffResponse.from(staffRepository.save(staff));
    }

    public List<StaffResponse> getAllBySchool(String schoolId) {
        schoolService.findById(schoolId);
        return staffRepository.findBySchoolIdAndActiveTrue(schoolId).stream()
                .map(StaffResponse::from)
                .toList();
    }

    public List<StaffResponse> search(String schoolId, String query) {
        schoolService.findById(schoolId);
        if (query == null || query.isBlank()) {
            return getAllBySchool(schoolId);
        }
        return staffRepository.searchByNameOrEmployeeId(schoolId, query).stream()
                .map(StaffResponse::from)
                .toList();
    }

    public StaffResponse getById(String schoolId, String staffId) {
        return StaffResponse.from(findByIdAndSchool(staffId, schoolId));
    }

    @Transactional
    public StaffResponse update(String schoolId, String staffId, UpdateStaffRequest request) {
        Staff staff = findByIdAndSchool(staffId, schoolId);

        if (request.getFirstName() != null) staff.setFirstName(request.getFirstName().trim());
        if (request.getLastName() != null) staff.setLastName(request.getLastName().trim());
        if (request.getFirstName() != null || request.getLastName() != null) {
            staff.setFullName(staff.getFirstName() + " " + staff.getLastName());
        }
        if (request.getDesignation() != null) staff.setDesignation(request.getDesignation());
        if (request.getSubjects() != null) staff.setSubjects(request.getSubjects());
        if (request.getQualification() != null) staff.setQualification(request.getQualification());
        if (request.getJoiningDate() != null) staff.setJoiningDate(request.getJoiningDate());
        if (request.getProfilePhotoUrl() != null) staff.setProfilePhotoUrl(request.getProfilePhotoUrl());
        if (request.getAddress() != null) staff.setAddress(request.getAddress());

        return StaffResponse.from(staffRepository.save(staff));
    }

    @Transactional
    public void deactivate(String schoolId, String staffId) {
        Staff staff = findByIdAndSchool(staffId, schoolId);
        staff.setActive(false);
        staffRepository.save(staff);
    }

    public TeacherProfileResponse getMyProfile(String userId) {
        Staff staff = staffRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException("Staff profile not found for this user", HttpStatus.NOT_FOUND));

        List<SectionResponse> sections = sectionRepository.findByClassTeacherId(userId).stream()
                .map(SectionResponse::from)
                .toList();

        return new TeacherProfileResponse(StaffResponse.from(staff), sections);
    }

    public Staff findByIdAndSchool(String staffId, String schoolId) {
        return staffRepository.findByIdAndSchoolId(staffId, schoolId)
                .orElseThrow(() -> new AppException("Staff member not found", HttpStatus.NOT_FOUND));
    }
}
