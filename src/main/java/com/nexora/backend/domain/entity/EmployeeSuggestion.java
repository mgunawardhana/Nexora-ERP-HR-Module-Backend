package com.nexora.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "emp_suggetions") // Fixed: matches your table name exactly
public class EmployeeSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", length = 100, columnDefinition = "VARCHAR(100) DEFAULT 'N/A'")
    private String firstName;

    @Column(name = "last_name", length = 100, columnDefinition = "VARCHAR(100) DEFAULT 'N/A'")
    private String lastName;

    @Column(name = "full_name", length = 200, nullable = false) // Fixed: added length constraint
    private String fullName;

    @Column(name = "department", length = 100, columnDefinition = "VARCHAR(100) DEFAULT 'N/A'")
    private String department;

    @Column(name = "employee_code", length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'N/A'")
    private String employeeCode;

    @Column(name = "suggestion", columnDefinition = "TEXT")
    private String suggestion;

    @Column(name = "saved_at", nullable = false, updatable = false)
    private LocalDateTime savedAt;

    @PrePersist
    protected void onCreate() {
        if (savedAt == null) {
            savedAt = LocalDateTime.now();
        }

        // Handle fullName generation more robustly
        if (fullName == null || fullName.trim().isEmpty()) {
            String first = (firstName != null && !firstName.trim().isEmpty()) ? firstName.trim() : "";
            String last = (lastName != null && !lastName.trim().isEmpty()) ? lastName.trim() : "";

            if (!first.isEmpty() && !last.isEmpty()) {
                fullName = first + " " + last;
            } else if (!first.isEmpty()) {
                fullName = first;
            } else if (!last.isEmpty()) {
                fullName = last;
            } else {
                fullName = "N/A";
            }
        }
    }
}