package com.school.school_app.service;

import com.school.school_app.dto.request.CreateStudentRequest;
import com.school.school_app.dto.request.LinkParentRequest;
import com.school.school_app.dto.request.TransferStudentRequest;
import com.school.school_app.dto.request.UpdateStudentRequest;
import com.school.school_app.dto.response.StudentResponse;
import com.school.school_app.entity.*;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.StudentRepository;
import com.school.school_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final SchoolService schoolService;
    private final AcademicYearService academicYearService;
    private final ClassRoomService classRoomService;
    private final SectionService sectionService;
    private final UserRepository userRepository;

    @Transactional
    public StudentResponse enroll(String schoolId, CreateStudentRequest request) {
        School school = schoolService.findById(schoolId);
        AcademicYear year = academicYearService.findByIdAndSchool(request.getAcademicYearId(), schoolId);
        ClassRoom classRoom = classRoomService.findByIdAndSchool(request.getClassRoomId(), schoolId);
        Section section = sectionService.findByIdAndClass(request.getSectionId(), classRoom.getId());

        if (studentRepository.existsBySchoolIdAndAdmissionNumber(schoolId, request.getAdmissionNumber())) {
            throw new AppException("Admission number '" + request.getAdmissionNumber() + "' already exists in this school", HttpStatus.CONFLICT);
        }

        if (studentRepository.existsBySchoolIdAndSectionIdAndRollNumber(schoolId, section.getId(), request.getRollNumber())) {
            throw new AppException("Roll number " + request.getRollNumber() + " already exists in this section", HttpStatus.CONFLICT);
        }

        String fullName = request.getFirstName().trim() + " " + request.getLastName().trim();

        Student.StudentBuilder builder = Student.builder()
                .schoolId(school.getId())
                .schoolName(school.getName())
                .academicYearId(year.getId())
                .academicYearLabel(year.getLabel())
                .classRoomId(classRoom.getId())
                .classRoomName(classRoom.getName())
                .sectionId(section.getId())
                .sectionName(section.getName())
                .admissionNumber(request.getAdmissionNumber())
                .rollNumber(request.getRollNumber())
                .admissionDate(request.getAdmissionDate())
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .fullName(fullName)
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .bloodGroup(request.getBloodGroup())
                .religion(request.getReligion())
                .category(request.getCategory())
                .address(request.getAddress())
                .active(true);

        if (request.getParentId() != null) {
            User parent = findParentById(request.getParentId());
            builder.parentId(parent.getId())
                    .parentName(parent.getFullName())
                    .parentPhone(parent.getPhone());
        }

        return StudentResponse.from(studentRepository.save(builder.build()));
    }

    public List<StudentResponse> getAllBySchool(String schoolId, String classRoomId, String sectionId) {
        schoolService.findById(schoolId);

        List<Student> students;
        if (sectionId != null) {
            students = studentRepository.findBySchoolIdAndSectionIdAndActiveTrue(schoolId, sectionId);
        } else if (classRoomId != null) {
            students = studentRepository.findBySchoolIdAndClassRoomIdAndActiveTrue(schoolId, classRoomId);
        } else {
            students = studentRepository.findBySchoolIdAndActiveTrue(schoolId);
        }

        return students.stream().map(StudentResponse::from).toList();
    }

    public StudentResponse getById(String schoolId, String studentId) {
        return StudentResponse.from(findByIdAndSchool(studentId, schoolId));
    }

    @Transactional
    public StudentResponse update(String schoolId, String studentId, UpdateStudentRequest request) {
        Student student = findByIdAndSchool(studentId, schoolId);

        if (request.getFirstName() != null) student.setFirstName(request.getFirstName().trim());
        if (request.getLastName() != null) student.setLastName(request.getLastName().trim());
        if (request.getFirstName() != null || request.getLastName() != null) {
            student.setFullName(student.getFirstName() + " " + student.getLastName());
        }
        if (request.getDateOfBirth() != null) student.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) student.setGender(request.getGender());
        if (request.getRollNumber() != null) {
            if (studentRepository.existsBySchoolIdAndSectionIdAndRollNumber(schoolId, student.getSectionId(), request.getRollNumber())) {
                throw new AppException("Roll number " + request.getRollNumber() + " already exists in this section", HttpStatus.CONFLICT);
            }
            student.setRollNumber(request.getRollNumber());
        }
        if (request.getBloodGroup() != null) student.setBloodGroup(request.getBloodGroup());
        if (request.getReligion() != null) student.setReligion(request.getReligion());
        if (request.getCategory() != null) student.setCategory(request.getCategory());
        if (request.getProfilePhotoUrl() != null) student.setProfilePhotoUrl(request.getProfilePhotoUrl());
        if (request.getAddress() != null) student.setAddress(request.getAddress());

        return StudentResponse.from(studentRepository.save(student));
    }

    @Transactional
    public void deactivate(String schoolId, String studentId) {
        Student student = findByIdAndSchool(studentId, schoolId);
        student.setActive(false);
        studentRepository.save(student);
    }

    public List<StudentResponse> search(String schoolId, String query) {
        schoolService.findById(schoolId);
        if (query == null || query.isBlank()) {
            return studentRepository.findBySchoolIdAndActiveTrue(schoolId).stream()
                    .map(StudentResponse::from).toList();
        }
        return studentRepository.searchByNameOrAdmission(schoolId, query).stream()
                .map(StudentResponse::from).toList();
    }

    @Transactional
    public StudentResponse linkParent(String schoolId, String studentId, LinkParentRequest request) {
        Student student = findByIdAndSchool(studentId, schoolId);
        User parent = findParentById(request.getParentId());

        student.setParentId(parent.getId());
        student.setParentName(parent.getFullName());
        student.setParentPhone(parent.getPhone());

        return StudentResponse.from(studentRepository.save(student));
    }

    @Transactional
    public StudentResponse transfer(String schoolId, String studentId, TransferStudentRequest request) {
        Student student = findByIdAndSchool(studentId, schoolId);
        ClassRoom newClass = classRoomService.findByIdAndSchool(request.getClassRoomId(), schoolId);
        Section newSection = sectionService.findByIdAndClass(request.getSectionId(), newClass.getId());

        if (studentRepository.existsBySchoolIdAndSectionIdAndRollNumber(schoolId, newSection.getId(), request.getRollNumber())) {
            throw new AppException("Roll number " + request.getRollNumber() + " already exists in target section", HttpStatus.CONFLICT);
        }

        student.setClassRoomId(newClass.getId());
        student.setClassRoomName(newClass.getName());
        student.setSectionId(newSection.getId());
        student.setSectionName(newSection.getName());
        student.setAcademicYearId(newClass.getAcademicYearId());
        student.setAcademicYearLabel(newClass.getAcademicYearLabel());
        student.setRollNumber(request.getRollNumber());

        return StudentResponse.from(studentRepository.save(student));
    }

    public List<StudentResponse> getMyChildren(String parentId) {
        return studentRepository.findByParentId(parentId).stream()
                .map(StudentResponse::from).toList();
    }

    public Student findByIdAndSchool(String studentId, String schoolId) {
        return studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new AppException("Student not found", HttpStatus.NOT_FOUND));
    }

    public Student findById(String studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException("Student not found", HttpStatus.NOT_FOUND));
    }

    private User findParentById(String parentId) {
        User parent = userRepository.findById(parentId)
                .orElseThrow(() -> new AppException("Parent not found", HttpStatus.NOT_FOUND));
        if (parent.getRole() != Role.PARENT) {
            throw new AppException("User is not a parent", HttpStatus.BAD_REQUEST);
        }
        return parent;
    }
}
