package com.nexora.backend.analytics.service.impl;

import com.nexora.backend.analytics.repository.AnalyticsRepository;
import com.nexora.backend.analytics.service.EmployeeDetailsService;
import com.nexora.backend.authentication.repository.EmployeeDetailsRepository;
import com.nexora.backend.domain.enums.EmploymentStatus;
import com.nexora.backend.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeDetailsServiceImpl implements EmployeeDetailsService {

    private final AnalyticsRepository analyticsRepository;

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

            locationRoleCountMap.computeIfAbsent(officeLocation, k -> new HashMap<>())
                    .put(role.name(), count);
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

            statusRoleCountMap.computeIfAbsent(status.name(), k -> new HashMap<>())
                    .put(role.name(), count);
        }

        return statusRoleCountMap;
    }
}
