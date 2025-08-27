package com.nexora.backend.analytics.controller;


import com.nexora.backend.analytics.service.EmployeeDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@RestController
@RequestMapping("api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final EmployeeDetailsService employeeDetailsService;


    @GetMapping("/role-count")
    public ResponseEntity<Map<String, Long>> getEmployeeCountByRole() {
        return ResponseEntity.ok(employeeDetailsService.getEmployeeCountByRole());
    }

    //TODO: here
    @GetMapping("/role-by-office-location")
    public ResponseEntity<Map<String, Map<String, Long>>> getEmployeeCountByRoleAndOfficeLocation() {
        return ResponseEntity.ok(employeeDetailsService.getEmployeeCountByRoleAndOfficeLocation());
    }

    @GetMapping("/role-by-employment-status")
    public ResponseEntity<Map<String, Map<String, Long>>> getEmployeeCountByRoleAndEmploymentStatus() {
        return ResponseEntity.ok(employeeDetailsService.getEmployeeCountByRoleAndEmploymentStatus());
    }

    @GetMapping("/export-employee-attendance-csv")
    public ResponseEntity<String> exportEmployeeAttendanceData() {
        try {
            String csvContent = employeeDetailsService.generateEmployeeAttendanceCsv();

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "employee_attendance_report_" + timestamp + ".csv";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvContent);

        } catch (Exception e) {
            log.error("Error generating CSV export", e);
            return ResponseEntity.internalServerError()
                    .body("Error generating CSV export: " + e.getMessage());
        }
    }
}
