package com.nexora.backend.analytics.service;

import com.nexora.backend.domain.entity.EmployeeKpiRecord;

import java.util.List;
import java.util.Map;

public interface EmployeeDetailsService {

    Map<String, Long> getEmployeeCountByRole();

    Map<String, Map<String, Long>> getEmployeeCountByRoleAndOfficeLocation();

    Map<String, Map<String, Long>> getEmployeeCountByRoleAndEmploymentStatus();

    String generateEmployeeAttendanceCsv();

//    List<EmployeeKpiRecord> saveEmployeeKPItoTheDatabase();
}
