package com.nexora.backend.attendence.service.impl;

import com.nexora.backend.attendence.repository.AttendanceRepository;
import com.nexora.backend.attendence.repository.SuggestionsRepo;
import com.nexora.backend.attendence.service.AttendanceService;
import com.nexora.backend.authentication.repository.UserRepository;
import com.nexora.backend.domain.entity.Attendance;
import com.nexora.backend.domain.entity.EmployeeSuggestion;
import com.nexora.backend.domain.entity.User;
import com.nexora.backend.domain.request.AttendanceRequest;
import com.nexora.backend.domain.response.APIResponse;
import com.nexora.backend.domain.response.SuggestionSaveRequest;
import com.nexora.backend.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final SuggestionsRepo suggestionsRepo;

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
            // Validate that email is provided in the request
            if (attendanceRequest.getEmail() == null || attendanceRequest.getEmail().trim().isEmpty()) {
                return responseUtil.wrapError("Email is required", "Please provide a valid email address", HttpStatus.BAD_REQUEST);
            }

            // Find user by email
            User user = userRepository.findByEmail(attendanceRequest.getEmail()).orElse(null);

            if (user == null) {
                return responseUtil.wrapError("User not found", "No user found with the provided email: " + attendanceRequest.getEmail(), HttpStatus.NOT_FOUND);
            }

            // Find existing attendance record for the user and current date
            Attendance existingAttendance = attendanceRepository.findByUserIdAndAttendanceDate(Long.valueOf(user.getId()), LocalDate.now()).orElse(null);

            Attendance attendance;

            if (existingAttendance != null) {
                // Update existing record
                existingAttendance.setStatus(attendanceRequest.getAttendanceStatus());
                existingAttendance.setCheckInTime(attendanceRequest.getCheckInTime() != null ? attendanceRequest.getCheckInTime() : existingAttendance.getCheckInTime());
                existingAttendance.setLunchOutTime(attendanceRequest.getLunchOutTime() != null ? attendanceRequest.getLunchOutTime() : existingAttendance.getLunchOutTime());
                existingAttendance.setLunchInTime(attendanceRequest.getLunchInTime() != null ? attendanceRequest.getLunchInTime() : existingAttendance.getLunchInTime());
                existingAttendance.setCheckOutTime(attendanceRequest.getCheckOutTime() != null ? attendanceRequest.getCheckOutTime() : existingAttendance.getCheckOutTime());
                existingAttendance.setNotes(attendanceRequest.getNotes() != null ? attendanceRequest.getNotes() : existingAttendance.getNotes());
                existingAttendance.setDailyWorkingHours(attendanceRequest.getDailyWorkingHours() != null ? attendanceRequest.getDailyWorkingHours() : existingAttendance.getDailyWorkingHours());
                existingAttendance.setCreatedAt(attendanceRequest.getCreatedAt() != null ? attendanceRequest.getCreatedAt() : existingAttendance.getCreatedAt());

                attendance = existingAttendance;
                log.info("Updating existing attendance for user: {}", user.getEmail());
            } else {
                // Create new record
                attendance = Attendance.builder().user(user).attendanceDate(LocalDate.now()).status(attendanceRequest.getAttendanceStatus()).checkInTime(attendanceRequest.getCheckInTime()).lunchOutTime(attendanceRequest.getLunchOutTime()).lunchInTime(attendanceRequest.getLunchInTime()).checkOutTime(attendanceRequest.getCheckOutTime()).notes(attendanceRequest.getNotes()).dailyWorkingHours(attendanceRequest.getDailyWorkingHours()).createdAt(attendanceRequest.getCreatedAt()).build();

                log.info("Creating new attendance record for user: {}", user.getEmail());
            }

            Attendance savedAttendance = attendanceRepository.save(attendance);
            log.info("Attendance saved successfully for user: {} with ID: {}", user.getEmail(), savedAttendance.getId());

            return responseUtil.wrapSuccess("Attendance saved successfully for user: " + user.getEmail(), HttpStatus.CREATED);

        } catch (Exception e) {
            log.error("Error processing attendance: {}", e.getMessage(), e);
            return responseUtil.wrapError("An error occurred while processing attendance", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<APIResponse> saveSuggestions(SuggestionSaveRequest suggestionSaveRequest) {

        EmployeeSuggestion suggestion = EmployeeSuggestion.builder().firstName(suggestionSaveRequest.firstName).lastName(suggestionSaveRequest.lastName).fullName(suggestionSaveRequest.firstName + " " + suggestionSaveRequest.lastName).department(suggestionSaveRequest.department).employeeCode(suggestionSaveRequest.employeeCode).suggestion(suggestionSaveRequest.suggestion).build();

        suggestionsRepo.save(suggestion);
        log.info("Suggestion saved for employee: {} {}", suggestionSaveRequest.firstName, suggestionSaveRequest.lastName);
        return responseUtil.wrapSuccess("Suggestion saved successfully", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<APIResponse> fetchSuggestions(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EmployeeSuggestion> suggestionsPage = suggestionsRepo.findAll(pageable);

        return responseUtil.wrapSuccess(suggestionsPage.getContent(), HttpStatus.OK);
    }
}