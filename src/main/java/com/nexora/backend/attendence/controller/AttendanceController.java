package com.nexora.backend.attendence.controller;

import com.nexora.backend.attendence.service.AttendanceService;
import com.nexora.backend.domain.request.AttendanceRequest;
import com.nexora.backend.domain.response.APIResponse;
import com.nexora.backend.domain.response.SuggestionSaveRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@RestController
@RequestMapping("api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    @NonNull
    private final AttendanceService attendanceService;

    @PostMapping("/mark-attendance")
    public ResponseEntity<APIResponse> markAttendance(@RequestBody AttendanceRequest attendanceRequest) {
        log.info("Marking attendance for user: {}", attendanceRequest.getUserId());
        return attendanceService.markDailyAttendance(attendanceRequest);
    }


    @PostMapping("/save-suggestions")
    public ResponseEntity<APIResponse> saveSuggestions(@RequestBody SuggestionSaveRequest request) {
        return attendanceService.saveSuggestions(request);
    }


    @PostMapping("/fetch-suggestions")
    public ResponseEntity<APIResponse> getAllUsers(
            @RequestParam Integer page,
            @RequestParam Integer size) {
        log.info("Get All Users");
        return attendanceService.fetchSuggestions(page, size);
    }

}