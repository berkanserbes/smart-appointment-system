package com.smartappointment.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Category entity for grouping appointments by type.
 * Examples: "Dentist", "Eye Doctor", "Business Meeting"
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    /**
     * Color code for UI display (e.g., "#3B82F6").
     * Used for category badges and cards in the frontend.
     */
    @Column(length = 7)
    @Builder.Default
    private String colorCode = "#3B82F6";

    /**
     * Default duration for appointments in this category (in minutes).
     * Examples: Dental checkup = 30, Eye exam = 20, Meeting = 60
     */
    @Column(nullable = false)
    @Builder.Default
    private int defaultDurationMinutes = 30;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
