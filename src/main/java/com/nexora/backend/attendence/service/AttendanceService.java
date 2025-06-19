package com.nexora.backend.attendence.service;

import com.nexora.backend.domain.request.AttendanceRequest;
import com.nexora.backend.domain.response.APIResponse;
import org.springframework.http.ResponseEntity;

public interface AttendanceService {

    ResponseEntity<APIResponse> markDailyAttendance(AttendanceRequest attendanceRequest);
}
