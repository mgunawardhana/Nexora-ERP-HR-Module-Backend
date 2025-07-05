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
//    private final RestTemplate restTemplate;
    private final AnalyticsRepository analyticsRepository;
    private final EmployeeKpiRecordRepository employeeDetailsRepository;

//    @Override
//    @Scheduled(cron = "0 * * * * ?") // Runs every minute for testing; adjust as needed (e.g., monthly)
//    public List<EmployeeKpiRecord> saveEmployeeKPItoTheDatabase() {
//        LocalDate currentDate = LocalDate.now();
//        int currentMonth = currentDate.getMonthValue();
//        int currentYear = currentDate.getYear();
//
//        // Check if records already exist for the given month and year
//        if (employeeDetailsRepository.existsByRecordMonthAndRecordYear(currentMonth, currentYear)) {
//            log.info("KPI records already exist for month {} and year {}. Skipping calculation.", currentMonth, currentYear);
//            return Collections.emptyList();
//        }
//
//        try {
//            String apiUrl = "http://localhost:8000/calculate-kpi";
//            ResponseEntity<KpiApiResponse> response = restTemplate.exchange(
//                    apiUrl, HttpMethod.GET, null, KpiApiResponse.class);
//
//            // Validate HTTP response
//            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
//                log.error("Failed to fetch KPI data from API. Status: {}", response.getStatusCode());
//                return Collections.emptyList();
//            }
//
//            KpiApiResponse kpiData = response.getBody();
//
//            // Validate API response status and data
//            if (!"success".equalsIgnoreCase(kpiData.getStatus()) || kpiData.getEmployees() == null) {
//                log.error("API returned error status: {} or null employee data", kpiData.getMessage());
//                return Collections.emptyList();
//            }
//
//            List<EmployeeKpiRecord> kpiRecords = new ArrayList<>();
//
//            // Map API response to EmployeeKpiRecord entities
//            for (KpiEmployeeData employee : kpiData.getEmployees()) {
//                if (employee.getName() == null || employee.getKpiPercentage() == null) {
//                    log.warn("Skipping invalid employee data: {}", employee);
//                    continue; // Skip invalid employee entries
//                }
//
//                EmployeeKpiRecord record = new EmployeeKpiRecord();
//                record.setEmployeeName(employee.getName());
//                record.setMonthlyKpi(employee.getMonthlyKpi() * 100); // Convert decimal to percentage
//                record.setTotalActualHours(employee.getTotalActualHours());
//                record.setTotalScheduledHours(employee.getTotalScheduledHours());
//                record.setWorkdayCount(employee.getWorkdayCount());
//                record.setKpiPercentage(employee.getKpiPercentage());
//                record.setRecordDate(currentDate);
//                record.setRecordMonth(currentMonth);
//                record.setRecordYear(currentYear);
//
//                kpiRecords.add(record);
//            }
//
//            // Save records to the database
//            if (!kpiRecords.isEmpty()) {
//                employeeDetailsRepository.saveAll(kpiRecords);
//                log.info("Successfully saved {} KPI records for month {} and year {}",
//                        kpiRecords.size(), currentMonth, currentYear);
//            } else {
//                log.warn("No valid KPI records to save for month {} and year {}", currentMonth, currentYear);
//            }
//
//            return kpiRecords;
//
//        } catch (RestClientException e) {
//            log.error("Error calling KPI API for month {} and year {}: {}",
//                    currentMonth, currentYear, e.getMessage(), e);
//            return Collections.emptyList();
//        } catch (Exception e) {
//            log.error("Unexpected error while processing KPI records for month {} and year {}: {}",
//                    currentMonth, currentYear, e.getMessage(), e);
//            return Collections.emptyList();
//        }
//    }

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
        csvBuilder.append("User ID,First Name,Last Name,Role,Department,Join Date,Current Salary,").append("Hourly Rate,Education Level,Previous Experience Years,Attendance Date,").append("Status,Check In Time,Lunch Out Time,Lunch In Time,Check Out Time,Daily Working Hours\n");

        // CSV Data Rows
        for (Object[] row : data) {
            csvBuilder.append(formatCsvValue(row[0]))    // User ID
                    .append(",").append(formatCsvValue(row[1]))    // First Name
                    .append(",").append(formatCsvValue(row[2]))    // Last Name
                    .append(",").append(formatCsvValue(row[3]))    // Role
                    .append(",").append(formatCsvValue(row[4]))    // Department
                    .append(",").append(formatCsvDate((LocalDate) row[5]))  // Join Date
                    .append(",").append(formatCsvValue(row[6]))    // Current Salary
                    .append(",").append(formatCsvValue(row[7]))    // Hourly Rate
                    .append(",").append(formatCsvValue(row[8]))    // Education Level
                    .append(",").append(formatCsvValue(row[9]))    // Previous Experience Years
                    .append(",").append(formatCsvDate((LocalDate) row[10])) // Attendance Date
                    .append(",").append(formatCsvValue(row[11]))   // Status
                    .append(",").append(formatCsvDateTime((LocalDateTime) row[12])) // Check In Time
                    .append(",").append(formatCsvDateTime((LocalDateTime) row[13])) // Lunch Out Time
                    .append(",").append(formatCsvDateTime((LocalDateTime) row[14])) // Lunch In Time
                    .append(",").append(formatCsvDateTime((LocalDateTime) row[15])) // Check Out Time
                    .append(",").append(formatCsvValue(row[16]))   // Daily Working Hours
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
