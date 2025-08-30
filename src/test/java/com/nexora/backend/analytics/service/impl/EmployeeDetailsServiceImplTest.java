package com.nexora.backend.analytics.service.impl;

import com.nexora.backend.analytics.repository.AnalyticsRepository;
import com.nexora.backend.domain.enums.EmploymentStatus;
import com.nexora.backend.domain.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeDetailsServiceImpl Tests")
class EmployeeDetailsServiceImplTest {

    @Mock
    private AnalyticsRepository analyticsRepository;

    @InjectMocks
    private EmployeeDetailsServiceImpl employeeDetailsService;

    private List<Object[]> mockRoleCountData;
    private List<Object[]> mockDepartmentRoleData;
    private List<Object[]> mockRoleEmploymentStatusData;

    @BeforeEach
    void setUp() {
        mockRoleCountData = Arrays.asList(new Object[]{Role.EMPLOYEE, 50L}, new Object[]{Role.DEPARTMENT_MANAGER, 10L});

        mockDepartmentRoleData = Arrays.asList(new Object[]{"IT", Role.EMPLOYEE, 25L}, new Object[]{"HR", Role.HR_MANAGER, 5L});

        mockRoleEmploymentStatusData = Arrays.asList(new Object[]{Role.EMPLOYEE, EmploymentStatus.ACTIVE, 45L}, new Object[]{Role.DEPARTMENT_MANAGER, EmploymentStatus.ACTIVE, 10L});
    }

    @Test
    @DisplayName("Success: Get Employee Count By Role")
    void getEmployeeCountByRole_Success() {
        when(analyticsRepository.findEmployeeCountByRole()).thenReturn(mockRoleCountData);
        Map<String, Long> result = employeeDetailsService.getEmployeeCountByRole();
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get("EMPLOYEE")).isEqualTo(50L);
        verify(analyticsRepository).findEmployeeCountByRole();
    }

    @Test
    @DisplayName("Error: Get Employee Count By Role with Database Exception")
    void getEmployeeCountByRole_RepositoryException() {
        when(analyticsRepository.findEmployeeCountByRole()).thenThrow(new RuntimeException("Database error"));
        assertThatThrownBy(() -> employeeDetailsService.getEmployeeCountByRole()).isInstanceOf(RuntimeException.class).hasMessage("Database error");
        verify(analyticsRepository).findEmployeeCountByRole();
    }

    @Test
    @DisplayName("Success: Get Employee Count By Role And Office Location")
    void getEmployeeCountByRoleAndOfficeLocation_Success() {
        when(analyticsRepository.findEmployeeCountByDepartmentAndRole()).thenReturn(mockDepartmentRoleData);
        Map<String, Map<String, Long>> result = employeeDetailsService.getEmployeeCountByRoleAndOfficeLocation();
        assertThat(result).isNotNull();
        assertThat(result.get("IT")).containsEntry("EMPLOYEE", 25L);
        assertThat(result.get("HR")).containsEntry("HR_MANAGER", 5L);
        verify(analyticsRepository).findEmployeeCountByDepartmentAndRole();
    }

    @Test
    @DisplayName("Error: Get Employee Count by Location with Database Exception")
    void getEmployeeCountByRoleAndOfficeLocation_RepositoryException() {
        when(analyticsRepository.findEmployeeCountByDepartmentAndRole()).thenThrow(new RuntimeException("Database error"));
        assertThatThrownBy(() -> employeeDetailsService.getEmployeeCountByRoleAndOfficeLocation()).isInstanceOf(RuntimeException.class).hasMessage("Database error");
        verify(analyticsRepository).findEmployeeCountByDepartmentAndRole();
    }

    @Test
    @DisplayName("Success: Get Employee Count By Role And Employment Status")
    void getEmployeeCountByRoleAndEmploymentStatus_Success() {
        when(analyticsRepository.findEmployeeCountByRoleAndEmploymentStatus()).thenReturn(mockRoleEmploymentStatusData);
        Map<String, Map<String, Long>> result = employeeDetailsService.getEmployeeCountByRoleAndEmploymentStatus();
        assertThat(result).isNotNull();
        assertThat(result.get("ACTIVE")).containsEntry("EMPLOYEE", 45L);
        verify(analyticsRepository).findEmployeeCountByRoleAndEmploymentStatus();
    }

    @Test
    @DisplayName("Error: Get Employee Count by Status with Database Exception")
    void getEmployeeCountByRoleAndEmploymentStatus_RepositoryException() {
        when(analyticsRepository.findEmployeeCountByRoleAndEmploymentStatus()).thenThrow(new RuntimeException("Database error"));
        assertThatThrownBy(() -> employeeDetailsService.getEmployeeCountByRoleAndEmploymentStatus()).isInstanceOf(RuntimeException.class).hasMessage("Database error");
        verify(analyticsRepository).findEmployeeCountByRoleAndEmploymentStatus();
    }
}