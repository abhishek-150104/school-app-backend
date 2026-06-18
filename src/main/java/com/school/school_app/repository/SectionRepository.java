package com.school.school_app.repository;

import com.school.school_app.entity.Section;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends MongoRepository<Section, String> {
    List<Section> findByClassRoomIdOrderByNameAsc(String classRoomId);
    Optional<Section> findByIdAndClassRoomId(String id, String classRoomId);
    boolean existsByClassRoomIdAndName(String classRoomId, String name);
    long countByClassRoomId(String classRoomId);
    List<Section> findByClassTeacherId(String classTeacherId);
}
