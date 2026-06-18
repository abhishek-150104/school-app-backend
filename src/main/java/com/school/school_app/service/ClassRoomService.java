package com.school.school_app.service;

import com.school.school_app.dto.request.CreateClassRoomRequest;
import com.school.school_app.dto.request.UpdateClassRoomRequest;
import com.school.school_app.dto.response.ClassRoomResponse;
import com.school.school_app.entity.AcademicYear;
import com.school.school_app.entity.ClassRoom;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.ClassRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassRoomService {

    private final ClassRoomRepository classRoomRepository;
    private final SchoolService schoolService;
    private final AcademicYearService academicYearService;

    @Transactional
    public ClassRoomResponse create(String schoolId, CreateClassRoomRequest request) {
        schoolService.findById(schoolId);
        AcademicYear academicYear = academicYearService.findByIdAndSchool(request.getAcademicYearId(), schoolId);

        if (classRoomRepository.existsBySchoolIdAndAcademicYearIdAndName(schoolId, academicYear.getId(), request.getName())) {
            throw new AppException("Class '" + request.getName() + "' already exists for this academic year", HttpStatus.CONFLICT);
        }

        ClassRoom classRoom = ClassRoom.builder()
                .schoolId(schoolId)
                .academicYearId(academicYear.getId())
                .academicYearLabel(academicYear.getLabel())
                .name(request.getName())
                .displayOrder(request.getDisplayOrder())
                .build();

        return ClassRoomResponse.from(classRoomRepository.save(classRoom));
    }

    public List<ClassRoomResponse> getAllBySchool(String schoolId, String academicYearId) {
        schoolService.findById(schoolId);
        List<ClassRoom> rooms = (academicYearId != null)
                ? classRoomRepository.findBySchoolIdAndAcademicYearIdOrderByDisplayOrderAscNameAsc(schoolId, academicYearId)
                : classRoomRepository.findBySchoolIdOrderByDisplayOrderAscNameAsc(schoolId);

        return rooms.stream().map(ClassRoomResponse::from).toList();
    }

    public ClassRoomResponse getById(String schoolId, String classId) {
        return ClassRoomResponse.from(findByIdAndSchool(classId, schoolId));
    }

    @Transactional
    public ClassRoomResponse update(String schoolId, String classId, UpdateClassRoomRequest request) {
        ClassRoom classRoom = findByIdAndSchool(classId, schoolId);

        if (request.getName() != null) classRoom.setName(request.getName());
        if (request.getDisplayOrder() != null) classRoom.setDisplayOrder(request.getDisplayOrder());

        return ClassRoomResponse.from(classRoomRepository.save(classRoom));
    }

    @Transactional
    public void delete(String schoolId, String classId) {
        ClassRoom classRoom = findByIdAndSchool(classId, schoolId);
        classRoomRepository.delete(classRoom);
    }

    public ClassRoom findByIdAndSchool(String classId, String schoolId) {
        return classRoomRepository.findByIdAndSchoolId(classId, schoolId)
                .orElseThrow(() -> new AppException("Class not found", HttpStatus.NOT_FOUND));
    }

    public ClassRoom findById(String classId) {
        return classRoomRepository.findById(classId)
                .orElseThrow(() -> new AppException("Class not found", HttpStatus.NOT_FOUND));
    }
}
