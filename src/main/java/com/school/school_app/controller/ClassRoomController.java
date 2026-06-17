package com.school.school_app.controller;

import com.school.school_app.dto.request.CreateClassRoomRequest;
import com.school.school_app.dto.request.UpdateClassRoomRequest;
import com.school.school_app.dto.response.ApiResponse;
import com.school.school_app.dto.response.ClassRoomResponse;
import com.school.school_app.service.ClassRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schools/{schoolId}/classes")
@RequiredArgsConstructor
public class ClassRoomController {

    private final ClassRoomService classRoomService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<ClassRoomResponse>> create(
            @PathVariable Long schoolId,
            @Valid @RequestBody CreateClassRoomRequest request) {
        ClassRoomResponse response = classRoomService.create(schoolId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Class created", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'TEACHER', 'PARENT')")
    public ResponseEntity<ApiResponse<List<ClassRoomResponse>>> getAll(
            @PathVariable Long schoolId,
            @RequestParam(required = false) Long academicYearId) {
        return ResponseEntity.ok(ApiResponse.success(classRoomService.getAllBySchool(schoolId, academicYearId)));
    }

    @GetMapping("/{classId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN', 'TEACHER', 'PARENT')")
    public ResponseEntity<ApiResponse<ClassRoomResponse>> getById(
            @PathVariable Long schoolId,
            @PathVariable Long classId) {
        return ResponseEntity.ok(ApiResponse.success(classRoomService.getById(schoolId, classId)));
    }

    @PutMapping("/{classId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<ClassRoomResponse>> update(
            @PathVariable Long schoolId,
            @PathVariable Long classId,
            @Valid @RequestBody UpdateClassRoomRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Class updated", classRoomService.update(schoolId, classId, request)));
    }

    @DeleteMapping("/{classId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long schoolId,
            @PathVariable Long classId) {
        classRoomService.delete(schoolId, classId);
        return ResponseEntity.ok(ApiResponse.success("Class deleted", null));
    }
}
