package com.nexora.backend.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is mandatory")
    private User user;

    @Column(name = "attendance_date", nullable = false)
    @NotNull(message = "Attendance date is mandatory")
    private LocalDate attendanceDate = LocalDate.now();

    @Column(name = "status", nullable = false)
    @NotNull(message = "Attendance status is mandatory")
    private String status = "PRESENT";

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "lunch_out_time")
    private LocalDateTime lunchOutTime;

    @Column(name = "lunch_in_time")
    private LocalDateTime lunchInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes = "";

    @Column(name = "daily_working_hours")
    private String dailyWorkingHours = "8:00";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    private void calculateDailyWorkingHours() {
        if (checkInTime != null && lunchOutTime != null && lunchInTime != null && checkOutTime != null) {
            try {
                // Validate timestamp order
                if (checkInTime.isAfter(lunchOutTime) || lunchOutTime.isAfter(lunchInTime) || lunchInTime.isAfter(checkOutTime)) {
                    this.dailyWorkingHours = "8:00"; // Invalid order, use default
                    return;
                }

                // Calculate morning session (checkInTime to lunchOutTime)
                Duration morningDuration = Duration.between(checkInTime, lunchOutTime);
                // Calculate afternoon session (lunchInTime to checkOutTime)
                Duration afternoonDuration = Duration.between(lunchInTime, checkOutTime);
                // Total working duration
                Duration totalDuration = morningDuration.plus(afternoonDuration);

                // Convert total duration to hours and minutes
                long totalMinutes = totalDuration.toMinutes();
                if (totalMinutes < 0) {
                    this.dailyWorkingHours = "8:00"; // Negative duration, use default
                    return;
                }
                long hours = totalMinutes / 60;
                long minutes = totalMinutes % 60;

                // Format as HH:MM
                this.dailyWorkingHours = String.format("%d:%02d", hours, minutes);
            } catch (Exception e) {
                this.dailyWorkingHours = "8:00"; // Exception occurred, use default
            }
        } else {
            this.dailyWorkingHours = "8:00"; // Missing timestamps, use default
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateDailyWorkingHours();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateDailyWorkingHours();
    }
}