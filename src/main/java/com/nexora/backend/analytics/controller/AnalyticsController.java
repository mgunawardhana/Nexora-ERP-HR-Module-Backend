package com.nexora.backend.analytics.controller;


import com.nexora.backend.analytics.service.EmployeeDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/role-by-office-location")
    public ResponseEntity<Map<String, Map<String, Long>>> getEmployeeCountByRoleAndOfficeLocation() {
        return ResponseEntity.ok(employeeDetailsService.getEmployeeCountByRoleAndOfficeLocation());
    }

    @GetMapping("/role-by-employment-status")
    public ResponseEntity<Map<String, Map<String, Long>>> getEmployeeCountByRoleAndEmploymentStatus() {
        return ResponseEntity.ok(employeeDetailsService.getEmployeeCountByRoleAndEmploymentStatus());
    }
}
