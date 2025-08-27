package com.nexora.backend.analytics.service.impl;

import com.nexora.backend.analytics.repository.AnalyticsRepository;
import com.nexora.backend.analytics.repository.EmployeeKpiRecordRepository;
import com.nexora.backend.analytics.service.EmployeeDetailsService;
import com.nexora.backend.domain.entity.EmployeeKpiRecord;
import com.nexora.backend.domain.enums.EmploymentStatus;
import com.nexora.backend.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeDetailsServiceImpl implements EmployeeDetailsService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final AnalyticsRepository analyticsRepository;
    private final EmployeeKpiRecordRepository employeeDetailsRepository;

    @Override
    public Map<String, Long> getEmployeeCountByRole() {
        List<Object[]> results = analyticsRepository.findEmployeeCountByRole();
        Map<String, Long> roleCountMap = new HashMap<>();

        for (Object[] result : results) {
            Role role = (Role) result[0];
            Long count = (Long) result[1];
            roleCountMap.put(role.name(), count);
        }

        return roleCountMap;
    }

    @Override
    public Map<String, Map<String, Long>> getEmployeeCountByRoleAndOfficeLocation() {
        List<Object[]> results = analyticsRepository.findEmployeeCountByRoleAndOfficeLocation();
        Map<String, Map<String, Long>> locationRoleCountMap = new HashMap<>();

        for (Object[] result : results) {
            Role role = (Role) result[0];
            String officeLocation = (String) result[1];
            Long count = (Long) result[2];

            locationRoleCountMap.computeIfAbsent(officeLocation, k -> new HashMap<>()).put(role.name(), count);
        }

        return locationRoleCountMap;
    }

    @Override
    public Map<String, Map<String, Long>> getEmployeeCountByRoleAndEmploymentStatus() {
        List<Object[]> results = analyticsRepository.findEmployeeCountByRoleAndEmploymentStatus();
        Map<String, Map<String, Long>> statusRoleCountMap = new HashMap<>();

        for (Object[] result : results) {
            Role role = (Role) result[0];
            EmploymentStatus status = (EmploymentStatus) result[1];
            Long count = (Long) result[2];

            statusRoleCountMap.computeIfAbsent(status.name(), k -> new HashMap<>()).put(role.name(), count);
        }

        return statusRoleCountMap;
    }

    @Override
    public String generateEmployeeAttendanceCsv() {
        List<Object[]> data = analyticsRepository.findEmployeeAttendanceData();

        StringBuilder csvBuilder = new StringBuilder();

        // CSV Headers
        csvBuilder.append("User ID,First Name,Last Name,Role,Department,Hourly Rate,Education Level,Previous Experience Years,Attendance Date,").append("Status,Check In Time,Lunch Out Time,Lunch In Time,Check Out Time,Daily Working Hours\n");

        // CSV Data Rows
        for (Object[] row : data) {
            csvBuilder.append(formatCsvValue(row[0]))    // User ID
                    .append(",").append(formatCsvValue(row[1]))    // First Name
                    .append(",").append(formatCsvValue(row[2]))    // Last Name
                    .append(",").append(formatCsvValue(row[3]))    // Role
                    .append(",").append(formatCsvValue(row[4]))    // Department
                    .append(",").append(formatCsvValue(row[5]))  // Hourly Rate
                    .append(",").append(formatCsvValue(row[6]))    // Education Level
                    .append(",").append(formatCsvValue(row[7]))    // Previous Experience Years
                    .append(",").append(formatCsvDate((LocalDate) row[8])) // Attendance Date
                    .append(",").append(formatCsvValue(row[9]))   // Status
                    .append(",").append(formatCsvDateTime((LocalDateTime) row[10])) // Check In Time
                    .append(",").append(formatCsvDateTime((LocalDateTime) row[11])) // Lunch Out Time
                    .append(",").append(formatCsvDateTime((LocalDateTime) row[12])) // Lunch In Time
                    .append(",").append(formatCsvDateTime((LocalDateTime) row[13])) // Check Out Time
                    .append(",").append(formatCsvValue(row[14]))   // Daily Working Hours
                    .append("\n");
        }

        return csvBuilder.toString();
    }

    private String formatCsvValue(Object value) {
        if (value == null) {
            return "";
        }

        String stringValue = value.toString();

        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).toPlainString();
        }

        if (value instanceof Role) {
            return ((Role) value).name();
        }

        if (stringValue.contains(",") || stringValue.contains("\"") || stringValue.contains("\n")) {
            stringValue = "\"" + stringValue.replace("\"", "\"\"") + "\"";
        }

        return stringValue;
    }

    private String formatCsvDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

    private String formatCsvDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATETIME_FORMATTER);
    }
}