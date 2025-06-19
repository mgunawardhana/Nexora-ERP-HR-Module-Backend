package com.nexora.backend.domain.request;

import com.nexora.backend.domain.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRequest {
    private Long id;

    private Integer userId;

    private LocalDate attendanceDate;

    private AttendanceStatus attendanceStatus;

    private LocalDateTime checkInTime;

    private LocalDateTime lunchOutTime;

    private LocalDateTime lunchInTime;

    private LocalDateTime checkOutTime;

    private String dailyWorkingHours;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
