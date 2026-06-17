package com.school.school_app.controller;

import com.school.school_app.dto.request.CreateClassRoomRequest;
import com.school.school_app.dto.request.UpdateClassRoomRequest;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.dto.response.ClassRoomResponse;
import com.school.school_app.service.ClassRoomService;
import com.school.school_app.service.SchoolContextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
public class ClassRoomController {

    private final ClassRoomService classRoomService;
    private final SchoolContextService schoolContextService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<ClassRoomResponse>> create(
            @Valid @RequestBody CreateClassRoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Class created",
                        classRoomService.create(schoolContextService.getSchoolId(), request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'TEACHER', 'PARENT')")
    public ResponseEntity<ApiResponse<List<ClassRoomResponse>>> getAll(
            @RequestParam(required = false) String academicYearId) {
        return ResponseEntity.ok(ApiResponse.success(
                classRoomService.getAllBySchool(schoolContextService.getSchoolId(), academicYearId)));
    }

    @GetMapping("/{classId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'TEACHER', 'PARENT')")
    public ResponseEntity<ApiResponse<ClassRoomResponse>> getById(@PathVariable String classId) {
        return ResponseEntity.ok(ApiResponse.success(
                classRoomService.getById(schoolContextService.getSchoolId(), classId)));
    }

    @PutMapping("/{classId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<ClassRoomResponse>> update(
            @PathVariable String classId,
            @Valid @RequestBody UpdateClassRoomRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Class updated",
                classRoomService.update(schoolContextService.getSchoolId(), classId, request)));
    }

    @DeleteMapping("/{classId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String classId) {
        classRoomService.delete(schoolContextService.getSchoolId(), classId);
        return ResponseEntity.ok(ApiResponse.success("Class deleted", null));
    }
}
