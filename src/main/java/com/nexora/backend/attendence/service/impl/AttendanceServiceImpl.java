package com.nexora.backend.attendence.service.impl;

import com.nexora.backend.attendence.repository.AttendanceRepository;
import com.nexora.backend.attendence.service.AttendanceService;
import com.nexora.backend.authentication.repository.UserRepository;
import com.nexora.backend.domain.entity.Attendance;
import com.nexora.backend.domain.request.AttendanceRequest;
import com.nexora.backend.domain.response.APIResponse;
import com.nexora.backend.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    @NonNull
    private final ResponseUtil responseUtil;

    @NonNull
    private final AttendanceRepository attendanceRepository;

    @NonNull
    private final UserRepository userRepository;

    /**
     * Marks or updates daily attendance for a user based on the provided attendance request.
     * If an attendance record exists for the user and date, it updates the record; otherwise, it creates a new one.
     *
     * @param attendanceRequest the request object containing attendance details
     * @return a {@link ResponseEntity} containing an {@link APIResponse} with the result of the attendance marking or updating
     */
    @Override
    public ResponseEntity<APIResponse> markDailyAttendance(AttendanceRequest attendanceRequest) {
        try {
            // Find existing attendance record for the user and current date
            Attendance existingAttendance = attendanceRepository.findByUserIdAndAttendanceDate(
                    Long.valueOf(attendanceRequest.getUserId()), LocalDate.now()).orElse(null);

            Attendance attendance;

            if (existingAttendance != null) {
                // Update existing record
                existingAttendance.setStatus(attendanceRequest.getAttendanceStatus());
                existingAttendance.setCheckInTime(attendanceRequest.getCheckInTime() != null ?
                        attendanceRequest.getCheckInTime() : existingAttendance.getCheckInTime());
                existingAttendance.setLunchOutTime(attendanceRequest.getLunchOutTime() != null ?
                        attendanceRequest.getLunchOutTime() : existingAttendance.getLunchOutTime());
                existingAttendance.setLunchInTime(attendanceRequest.getLunchInTime() != null ?
                        attendanceRequest.getLunchInTime() : existingAttendance.getLunchInTime());
                existingAttendance.setCheckOutTime(attendanceRequest.getCheckOutTime() != null ?
                        attendanceRequest.getCheckOutTime() : existingAttendance.getCheckOutTime());
                existingAttendance.setNotes(attendanceRequest.getNotes() != null ?
                        attendanceRequest.getNotes() : existingAttendance.getNotes());
                existingAttendance.setDailyWorkingHours(attendanceRequest.getDailyWorkingHours() != null ?
                        attendanceRequest.getDailyWorkingHours() : existingAttendance.getDailyWorkingHours());
                existingAttendance.setCreatedAt(attendanceRequest.getCreatedAt() != null ?
                        attendanceRequest.getCreatedAt() : existingAttendance.getCreatedAt());

                attendance = existingAttendance;
            } else {
                // Create new record
                attendance = Attendance.builder()
                        .user(userRepository.findById(attendanceRequest.getUserId()).orElse(null))
                        .attendanceDate(LocalDate.now())
                        .status(attendanceRequest.getAttendanceStatus())
                        .checkInTime(attendanceRequest.getCheckInTime())
                        .lunchOutTime(attendanceRequest.getLunchOutTime())
                        .lunchInTime(attendanceRequest.getLunchInTime())
                        .checkOutTime(attendanceRequest.getCheckOutTime())
                        .notes(attendanceRequest.getNotes())
                        .dailyWorkingHours(attendanceRequest.getDailyWorkingHours())
                        .createdAt(attendanceRequest.getCreatedAt())
                        .build();
            }

            Attendance savedAttendance = attendanceRepository.save(attendance);
            log.info("Attendance saved successfully: {}",savedAttendance);

            return responseUtil.wrapSuccess("Attendance save successfully", HttpStatus.CREATED);

        } catch (Exception e) {
            System.err.println("Error processing attendance: " + e.getMessage());
            return responseUtil.wrapError("An error occurred while processing attendance: ", e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}