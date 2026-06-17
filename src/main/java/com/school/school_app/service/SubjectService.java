package com.school.school_app.service;

import com.school.school_app.dto.request.CreateSubjectRequest;
import com.school.school_app.dto.response.SubjectResponse;
import com.school.school_app.entity.ClassRoom;
import com.school.school_app.entity.Subject;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final ClassRoomService classRoomService;

    @Transactional
    public SubjectResponse create(String schoolId, String classId,
                                  CreateSubjectRequest request,
                                  String createdById, String createdByName) {
        ClassRoom classRoom = classRoomService.findByIdAndSchool(classId, schoolId);

        if (subjectRepository.existsByClassRoomIdAndName(classId, request.getName())) {
            throw new AppException(
                    "Subject '" + request.getName() + "' already exists for this class",
                    HttpStatus.CONFLICT);
        }

        Subject subject = Subject.builder()
                .schoolId(schoolId)
                .classRoomId(classId)
                .classRoomName(classRoom.getName())
                .name(request.getName())
                .code(request.getCode())
                .createdById(createdById)
                .createdByName(createdByName)
                .build();

        return SubjectResponse.from(subjectRepository.save(subject));
    }

    public List<SubjectResponse> getByClassRoom(String schoolId, String classId) {
        classRoomService.findByIdAndSchool(classId, schoolId);
        return subjectRepository.findByClassRoomIdOrderByNameAsc(classId)
                .stream().map(SubjectResponse::from).toList();
    }

    @Transactional
    public void delete(String schoolId, String classId, String subjectId) {
        classRoomService.findByIdAndSchool(classId, schoolId);
        Subject subject = subjectRepository.findByIdAndSchoolId(subjectId, schoolId)
                .orElseThrow(() -> new AppException("Subject not found", HttpStatus.NOT_FOUND));
        subjectRepository.delete(subject);
    }

    public List<Subject> findByClassRoomId(String classRoomId) {
        return subjectRepository.findByClassRoomIdOrderByNameAsc(classRoomId);
    }
}
