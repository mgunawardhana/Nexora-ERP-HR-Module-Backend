package com.nexora.backend.analytics.service.impl;

import com.nexora.backend.analytics.repository.AnalyticsRepository;
import com.nexora.backend.analytics.service.EmployeeDetailsService;
import com.nexora.backend.authentication.repository.EmployeeDetailsRepository;
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
}
