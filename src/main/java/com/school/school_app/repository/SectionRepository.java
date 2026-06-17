package com.school.school_app.repository;

import com.school.school_app.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByClassRoomIdOrderByNameAsc(Long classRoomId);

    Optional<Section> findByIdAndClassRoomId(Long id, Long classRoomId);

    boolean existsByClassRoomIdAndName(Long classRoomId, String name);

    long countByClassRoomId(Long classRoomId);
}
