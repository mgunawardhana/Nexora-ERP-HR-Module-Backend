package com.nexora.backend.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee_suggestions")
public class EmployeeSuggestion {

    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @Column(name = "full_name", nullable = false)
    @NotBlank(message = "Full name is mandatory")
    private String fullName;

    @Column(name = "department")
    private String department;

    @Column(name = "employee_code")
    private String employeeCode;

    @Column(name = "suggestion", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Suggestion is mandatory")
    private String suggestion;

    @Column(name = "created_at", nullable = false, updatable = false)
    @NotNull
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();

        if (fullName == null || fullName.trim().isEmpty()) {
            fullName = String.format("%s %s",
                    firstName != null ? firstName.trim() : "",
                    lastName != null ? lastName.trim() : "").trim();

            if (fullName.isEmpty()) {
                fullName = "N/A";
            }
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}