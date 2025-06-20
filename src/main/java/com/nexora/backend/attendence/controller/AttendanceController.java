package com.nexora.backend.attendence.controller;

import com.nexora.backend.attendence.service.AttendanceService;
import com.nexora.backend.domain.request.AttendanceRequest;
import com.nexora.backend.domain.response.APIResponse;
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
}