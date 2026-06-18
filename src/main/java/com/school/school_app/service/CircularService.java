package com.school.school_app.service;

import com.school.school_app.dto.request.CreateCircularRequest;
import com.school.school_app.dto.response.CircularResponse;
import com.school.school_app.entity.Circular;
import com.school.school_app.entity.CircularRead;
import com.school.school_app.exception.AppException;
import com.school.school_app.repository.CircularReadRepository;
import com.school.school_app.repository.CircularRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CircularService {

    private final CircularRepository circularRepository;
    private final CircularReadRepository circularReadRepository;

    public CircularResponse publish(String schoolId, String schoolName,
                                    CreateCircularRequest request,
                                    String publisherId, String publisherName) {
        Circular c = Circular.builder()
                .schoolId(schoolId).schoolName(schoolName)
                .title(request.getTitle()).content(request.getContent())
                .targetType(request.getTargetType())
                .targetClassRoomId(request.getTargetClassRoomId())
                .targetSectionId(request.getTargetSectionId())
                .publishedById(publisherId).publishedByName(publisherName)
                .publishedAt(LocalDateTime.now())
                .build();
        c = circularRepository.save(c);
        return CircularResponse.from(c, false);
    }

    public List<CircularResponse> getAll(String schoolId, String userId) {
        Set<String> readIds = circularReadRepository.findByUserId(userId)
                .stream().map(CircularRead::getCircularId).collect(Collectors.toSet());
        return circularRepository.findBySchoolIdOrderByPublishedAtDesc(schoolId)
                .stream().map(c -> CircularResponse.from(c, readIds.contains(c.getId()))).toList();
    }

    public CircularResponse getById(String schoolId, String circularId, String userId) {
        Circular c = circularRepository.findByIdAndSchoolId(circularId, schoolId)
                .orElseThrow(() -> new AppException("Circular not found", HttpStatus.NOT_FOUND));
        boolean read = circularReadRepository.existsByCircularIdAndUserId(circularId, userId);
        return CircularResponse.from(c, read);
    }

    public CircularResponse update(String schoolId, String circularId,
                                   CreateCircularRequest request, String requesterId) {
        Circular c = circularRepository.findByIdAndSchoolId(circularId, schoolId)
                .orElseThrow(() -> new AppException("Circular not found", HttpStatus.NOT_FOUND));
        if (!c.getPublishedById().equals(requesterId)) {
            throw new AppException("Only the publisher can update this circular", HttpStatus.FORBIDDEN);
        }
        c.setTitle(request.getTitle());
        c.setContent(request.getContent());
        c.setTargetType(request.getTargetType());
        c.setTargetClassRoomId(request.getTargetClassRoomId());
        c.setTargetSectionId(request.getTargetSectionId());
        c = circularRepository.save(c);
        boolean read = circularReadRepository.existsByCircularIdAndUserId(circularId, requesterId);
        return CircularResponse.from(c, read);
    }

    public void delete(String schoolId, String circularId) {
        Circular c = circularRepository.findByIdAndSchoolId(circularId, schoolId)
                .orElseThrow(() -> new AppException("Circular not found", HttpStatus.NOT_FOUND));
        circularRepository.delete(c);
    }

    public void markRead(String circularId, String userId) {
        CircularRead cr = circularReadRepository
                .findByCircularIdAndUserId(circularId, userId)
                .orElse(CircularRead.builder().circularId(circularId).userId(userId).build());
        cr.setReadAt(LocalDateTime.now());
        circularReadRepository.save(cr);
    }

    public int getUnreadCount(String schoolId, String userId) {
        Set<String> readIds = circularReadRepository.findByUserId(userId)
                .stream().map(CircularRead::getCircularId).collect(Collectors.toSet());
        List<Circular> all = circularRepository.findBySchoolIdOrderByPublishedAtDesc(schoolId);
        return (int) all.stream().filter(c -> !readIds.contains(c.getId())).count();
    }
}
