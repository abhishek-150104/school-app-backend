package com.school.school_app.service;

import com.school.school_app.dto.request.AssignTeacherRequest;
import com.school.school_app.dto.request.CreateSectionRequest;
import com.school.school_app.dto.request.UpdateSectionRequest;
import com.school.school_app.dto.response.SectionResponse;
import com.school.school_app.entity.ClassRoom;
import com.school.school_app.entity.Role;
import com.school.school_app.entity.Section;
import com.school.school_app.entity.User;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.SectionRepository;
import com.school.school_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;
    private final ClassRoomService classRoomService;
    private final UserRepository userRepository;

    @Transactional
    public SectionResponse create(String classId, CreateSectionRequest request) {
        ClassRoom classRoom = classRoomService.findById(classId);

        if (sectionRepository.existsByClassRoomIdAndName(classId, request.getName())) {
            throw new AppException("Section '" + request.getName() + "' already exists in this class", HttpStatus.CONFLICT);
        }

        Section section = Section.builder()
                .classRoomId(classRoom.getId())
                .classRoomName(classRoom.getName())
                .name(request.getName())
                .capacity(request.getCapacity())
                .build();

        return SectionResponse.from(sectionRepository.save(section));
    }

    public List<SectionResponse> getAllByClass(String classId) {
        classRoomService.findById(classId);
        return sectionRepository.findByClassRoomIdOrderByNameAsc(classId).stream()
                .map(SectionResponse::from)
                .toList();
    }

    public SectionResponse getById(String classId, String sectionId) {
        return SectionResponse.from(findByIdAndClass(sectionId, classId));
    }

    @Transactional
    public SectionResponse update(String classId, String sectionId, UpdateSectionRequest request) {
        Section section = findByIdAndClass(sectionId, classId);

        if (request.getName() != null) section.setName(request.getName());
        if (request.getCapacity() != null) section.setCapacity(request.getCapacity());

        return SectionResponse.from(sectionRepository.save(section));
    }

    @Transactional
    public SectionResponse assignTeacher(String classId, String sectionId, AssignTeacherRequest request) {
        Section section = findByIdAndClass(sectionId, classId);

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new AppException("Teacher not found", HttpStatus.NOT_FOUND));

        if (teacher.getRole() != Role.TEACHER) {
            throw new AppException("User is not a teacher", HttpStatus.BAD_REQUEST);
        }

        section.setClassTeacherId(teacher.getId());
        section.setClassTeacherName(teacher.getFullName());
        return SectionResponse.from(sectionRepository.save(section));
    }

    @Transactional
    public void delete(String classId, String sectionId) {
        Section section = findByIdAndClass(sectionId, classId);
        sectionRepository.delete(section);
    }

    public Section findByIdAndClass(String sectionId, String classId) {
        return sectionRepository.findByIdAndClassRoomId(sectionId, classId)
                .orElseThrow(() -> new AppException("Section not found", HttpStatus.NOT_FOUND));
    }

    public Section findById(String sectionId) {
        return sectionRepository.findById(sectionId)
                .orElseThrow(() -> new AppException("Section not found", HttpStatus.NOT_FOUND));
    }
}
