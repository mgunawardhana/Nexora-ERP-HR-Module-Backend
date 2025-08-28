package com.nexora.backend.attendence.service.impl;

import com.nexora.backend.attendence.repository.AttendanceRepository;
import com.nexora.backend.authentication.repository.UserRepository;
import com.nexora.backend.domain.entity.Attendance;
import com.nexora.backend.domain.entity.User;
import com.nexora.backend.domain.request.AttendanceRequest;
import com.nexora.backend.domain.response.APIResponse;
import com.nexora.backend.util.ResponseUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AttendanceServiceImpl Tests")
class AttendanceServiceImplTest {

    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ResponseUtil responseUtil;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    private User user;
    private AttendanceRequest request;
    private Attendance existingAttendance;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1).email("test@example.com").build();
        request = AttendanceRequest.builder()
                .userId(1)
                .attendanceStatus("PRESENT")
                .checkInTime(LocalDateTime.now())
                .build();
        existingAttendance = Attendance.builder()
                .id(1L)
                .user(user)
                .attendanceDate(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("Success: Mark New Daily Attendance")
    void markDailyAttendance_NewRecord_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(attendanceRepository.findByUserIdAndAttendanceDate(1L, LocalDate.now())).thenReturn(Optional.empty());
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(new Attendance());
        when(responseUtil.wrapSuccess(any(), any(HttpStatus.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());
        ResponseEntity<APIResponse> response = attendanceService.markDailyAttendance(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("Success: Update Existing Daily Attendance")
    void markDailyAttendance_UpdateRecord_Success() {
        when(attendanceRepository.findByUserIdAndAttendanceDate(1L, LocalDate.now()))
                .thenReturn(Optional.of(existingAttendance));
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(existingAttendance);
        when(responseUtil.wrapSuccess(any(), any(HttpStatus.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());
        request.setCheckOutTime(LocalDateTime.now());
        ResponseEntity<APIResponse> response = attendanceService.markDailyAttendance(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("Error: Mark Attendance with Database Exception")
    void markDailyAttendance_RepositoryException() {
        when(attendanceRepository.findByUserIdAndAttendanceDate(1L, LocalDate.now())).thenThrow(new RuntimeException("Database error"));
        when(responseUtil.wrapError(anyString(), anyString(), any(HttpStatus.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        ResponseEntity<APIResponse> response = attendanceService.markDailyAttendance(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}