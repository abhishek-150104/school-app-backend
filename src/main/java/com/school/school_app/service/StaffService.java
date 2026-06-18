package com.school.school_app.service;

import com.school.school_app.dto.request.CreateStaffRequest;
import com.school.school_app.dto.request.UpdateStaffRequest;
import com.school.school_app.dto.response.EnrollStaffResponse;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";

    private final StaffRepository staffRepository;
    private final SchoolService schoolService;
    private final UserRepository userRepository;
    private final SectionRepository sectionRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public EnrollStaffResponse create(String schoolId, CreateStaffRequest request) {
        School school = schoolService.findById(schoolId);

        if (staffRepository.existsBySchoolIdAndEmployeeId(schoolId, request.getEmployeeId())) {
            throw new AppException(
                    "Employee ID '" + request.getEmployeeId() + "' already exists in this school",
                    HttpStatus.CONFLICT);
        }

        User user;
        String tempPassword = null;

        if (request.getUserId() != null) {
            // Link existing user
            if (staffRepository.existsByUserId(request.getUserId())) {
                throw new AppException("This user already has a staff profile", HttpStatus.CONFLICT);
            }
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
            if (user.getRole() != Role.TEACHER) {
                throw new AppException("User must have TEACHER role to be added as staff", HttpStatus.BAD_REQUEST);
            }
        } else {
            // Auto-create user account with employeeId as login
            if (userRepository.existsByUsername(request.getEmployeeId())) {
                throw new AppException(
                        "Employee ID '" + request.getEmployeeId() + "' is already used as a login ID",
                        HttpStatus.CONFLICT);
            }
            if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
                throw new AppException("Email already in use", HttpStatus.CONFLICT);
            }
            if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
                throw new AppException("Phone already in use", HttpStatus.CONFLICT);
            }

            String fullName = request.getFirstName().trim() + " " + request.getLastName().trim();
            tempPassword = generateTempPassword();

            user = User.builder()
                    .fullName(fullName)
                    .username(request.getEmployeeId())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .password(passwordEncoder.encode(tempPassword))
                    .role(Role.TEACHER)
                    .enabled(true)
                    .passwordChanged(false)
                    .build();
            user = userRepository.save(user);
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

        return EnrollStaffResponse.builder()
                .staff(StaffResponse.from(staffRepository.save(staff)))
                .loginId(request.getEmployeeId())
                .tempPassword(tempPassword)
                .build();
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

    private String generateTempPassword() {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
