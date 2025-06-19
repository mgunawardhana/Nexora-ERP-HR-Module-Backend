package com.nexora.backend.attendence.service.impl;

import com.nexora.backend.attendence.repository.AttendanceRepository;
import com.nexora.backend.attendence.service.AttendanceService;
import com.nexora.backend.authentication.repository.UserRepository;
import com.nexora.backend.domain.entity.Attendance;
import com.nexora.backend.domain.entity.User;
import com.nexora.backend.domain.enums.AttendanceStatus;
import com.nexora.backend.domain.request.AttendanceRequest;
import com.nexora.backend.domain.response.APIResponse;
import com.nexora.backend.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    @NonNull
    private final AttendanceRepository attendanceRepository;

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final ResponseUtil responseUtil;

    @NonNull
    private final JdbcTemplate writeJdbcTemplate;

    @NonNull
    private final JdbcTemplate readJdbcTemplate;

    /**
     * Marks daily attendance for a user based on the provided attendance request.
     *
     * @param attendanceRequest the request object containing attendance details
     * @return a {@link ResponseEntity} containing an {@link APIResponse} with the result of the attendance marking
     */
    // Do NOT set id when building Attendance for new records
    public ResponseEntity<APIResponse> markDailyAttendance(AttendanceRequest attendanceRequest) {

        log.info("attendanceRequest.getUserId() : {}", attendanceRequest);


            User user = userRepository.findById(attendanceRequest.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + attendanceRequest.getUserId()));

            return null;
    }
}
