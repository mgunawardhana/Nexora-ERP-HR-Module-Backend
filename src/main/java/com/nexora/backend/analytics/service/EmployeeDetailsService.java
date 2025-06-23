package com.nexora.backend.analytics.service;

import java.util.Map;

public interface EmployeeDetailsService {

    Map<String, Long> getEmployeeCountByRole();

    Map<String, Map<String, Long>> getEmployeeCountByRoleAndOfficeLocation();

    Map<String, Map<String, Long>> getEmployeeCountByRoleAndEmploymentStatus();
}
