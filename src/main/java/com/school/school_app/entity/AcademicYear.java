package com.school.school_app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "academic_years",
    uniqueConstraints = @UniqueConstraint(columnNames = {"school_id", "label"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    // e.g. "2024-25"
    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private int startYear;

    @Column(nullable = false)
    private int endYear;

    @Builder.Default
    private boolean active = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
