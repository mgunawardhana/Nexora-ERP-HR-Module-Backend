package com.nexora.backend.analytics.service.impl;

import com.nexora.backend.analytics.repository.AnalyticsRepository;
import com.nexora.backend.analytics.repository.EmployeeKpiRecordRepository;
import com.nexora.backend.domain.enums.EmploymentStatus;
import com.nexora.backend.domain.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeDetailsService Implementation Tests")
class EmployeeDetailsServiceImplTest {

    @Mock
    private AnalyticsRepository analyticsRepository;

    @Mock
    private EmployeeKpiRecordRepository employeeKpiRecordRepository;

    @InjectMocks
    private EmployeeDetailsServiceImpl employeeDetailsService;

    private List<Object[]> mockRoleCountData;
    private List<Object[]> mockDepartmentRoleData;
    private List<Object[]> mockRoleEmploymentStatusData;

    @BeforeEach
    void setUp() {
        // Setup mock data for role count
        mockRoleCountData = Arrays.asList(
                new Object[]{Role.EMPLOYEE, 50L},
                new Object[]{Role.DEPARTMENT_MANAGER, 10L},
                new Object[]{Role.HR_MANAGER, 5L}
        );

        // Setup mock data for department and role
        mockDepartmentRoleData = Arrays.asList(
                new Object[]{"IT", Role.EMPLOYEE, 25L},
                new Object[]{"HR", Role.HR_MANAGER, 5L},
                new Object[]{"Finance", Role.EMPLOYEE, 15L}
        );

        // Setup mock data for role and employment status
        mockRoleEmploymentStatusData = Arrays.asList(
                new Object[]{Role.EMPLOYEE, EmploymentStatus.ACTIVE, 45L},
                new Object[]{Role.EMPLOYEE, EmploymentStatus.PROBATION, 5L},
                new Object[]{Role.DEPARTMENT_MANAGER, EmploymentStatus.ACTIVE, 10L}
        );
    }

    @Test
    @DisplayName("Should successfully get employee count by role")
    void getEmployeeCountByRole_Success() {
        // Given
        when(analyticsRepository.findEmployeeCountByRole()).thenReturn(mockRoleCountData);

        // When
        Map<String, Long> result = employeeDetailsService.getEmployeeCountByRole();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result.get("EMPLOYEE")).isEqualTo(50L);
        assertThat(result.get("DEPARTMENT_MANAGER")).isEqualTo(10L);
        assertThat(result.get("HR_MANAGER")).isEqualTo(5L);

        verify(analyticsRepository, times(1)).findEmployeeCountByRole();
    }

    @Test
    @DisplayName("Should return empty map when no role data found")
    void getEmployeeCountByRole_EmptyData() {
        // Given
        when(analyticsRepository.findEmployeeCountByRole()).thenReturn(Collections.emptyList());

        // When
        Map<String, Long> result = employeeDetailsService.getEmployeeCountByRole();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(analyticsRepository, times(1)).findEmployeeCountByRole();
    }

    @Test
    @DisplayName("Should throw exception when repository fails for role count")
    void getEmployeeCountByRole_RepositoryException() {
        // Given
        when(analyticsRepository.findEmployeeCountByRole())
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        assertThatThrownBy(() -> employeeDetailsService.getEmployeeCountByRole())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database connection failed");

        verify(analyticsRepository, times(1)).findEmployeeCountByRole();
    }

    @Test
    @DisplayName("Should successfully get employee count by role and office location")
    void getEmployeeCountByRoleAndOfficeLocation_Success() {
        // Given
        when(analyticsRepository.findEmployeeCountByDepartmentAndRole()).thenReturn(mockDepartmentRoleData);

        // When
        Map<String, Map<String, Long>> result = employeeDetailsService.getEmployeeCountByRoleAndOfficeLocation();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result.get("IT")).containsEntry("EMPLOYEE", 25L);
        assertThat(result.get("HR")).containsEntry("HR_MANAGER", 5L);
        assertThat(result.get("Finance")).containsEntry("EMPLOYEE", 15L);

        verify(analyticsRepository, times(1)).findEmployeeCountByDepartmentAndRole();
    }

    @Test
    @DisplayName("Should return empty map when no department role data found")
    void getEmployeeCountByRoleAndOfficeLocation_EmptyData() {
        // Given
        when(analyticsRepository.findEmployeeCountByDepartmentAndRole()).thenReturn(Collections.emptyList());

        // When
        Map<String, Map<String, Long>> result = employeeDetailsService.getEmployeeCountByRoleAndOfficeLocation();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(analyticsRepository, times(1)).findEmployeeCountByDepartmentAndRole();
    }

    @Test
    @DisplayName("Should successfully get employee count by role and employment status")
    void getEmployeeCountByRoleAndEmploymentStatus_Success() {
        // Given
        when(analyticsRepository.findEmployeeCountByRoleAndEmploymentStatus()).thenReturn(mockRoleEmploymentStatusData);

        // When
        Map<String, Map<String, Long>> result = employeeDetailsService.getEmployeeCountByRoleAndEmploymentStatus();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2); // ACTIVE and PROBATION
        assertThat(result.get("ACTIVE")).containsEntry("EMPLOYEE", 45L);
        assertThat(result.get("ACTIVE")).containsEntry("DEPARTMENT_MANAGER", 10L);
        assertThat(result.get("PROBATION")).containsEntry("EMPLOYEE", 5L);

        verify(analyticsRepository, times(1)).findEmployeeCountByRoleAndEmploymentStatus();
    }

    @Test
    @DisplayName("Should handle null values in employment status data")
    void getEmployeeCountByRoleAndEmploymentStatus_WithNullValues() {
        // Given
        List<Object[]> dataWithNulls = Arrays.asList(
                new Object[]{Role.EMPLOYEE, null, 10L},
                new Object[]{Role.EMPLOYEE, EmploymentStatus.ACTIVE, 40L}
        );
        when(analyticsRepository.findEmployeeCountByRoleAndEmploymentStatus()).thenReturn(dataWithNulls);

        // When & Then
        assertThatThrownBy(() -> employeeDetailsService.getEmployeeCountByRoleAndEmploymentStatus())
                .isInstanceOf(NullPointerException.class);

        verify(analyticsRepository, times(1)).findEmployeeCountByRoleAndEmploymentStatus();
    }

    @Test
    @DisplayName("Should generate CSV with empty data gracefully")
    void generateEmployeeAttendanceCsv_EmptyData() {
        // When
        String csvResult = employeeDetailsService.generateEmployeeAttendanceCsv();

        // Then
        assertThat(csvResult).isNotNull();
        assertThat(csvResult).contains("User ID,First Name,Last Name,Role,Department");
        assertThat(csvResult).contains("Status,Check In Time,Lunch Out Time");

        // Should only contain headers since data is null/empty
        String[] lines = csvResult.split("\n");
        assertThat(lines).hasSize(1); // Only header line
    }

    @Test
    @DisplayName("Should handle repository exception in employment status query")
    void getEmployeeCountByRoleAndEmploymentStatus_RepositoryException() {
        // Given
        when(analyticsRepository.findEmployeeCountByRoleAndEmploymentStatus())
                .thenThrow(new RuntimeException("SQL syntax error"));

        // When & Then
        assertThatThrownBy(() -> employeeDetailsService.getEmployeeCountByRoleAndEmploymentStatus())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("SQL syntax error");

        verify(analyticsRepository, times(1)).findEmployeeCountByRoleAndEmploymentStatus();
    }

    @Test
    @DisplayName("Should handle casting exception in department role query")
    void getEmployeeCountByRoleAndOfficeLocation_CastingException() {
        // Given
        List<Object[]> invalidData = Arrays.asList(
                new Object[]{"IT", "INVALID_ROLE", 25L} // Invalid role that can't be cast
        );
        when(analyticsRepository.findEmployeeCountByDepartmentAndRole()).thenReturn(invalidData);

        // When & Then
        assertThatThrownBy(() -> employeeDetailsService.getEmployeeCountByRoleAndOfficeLocation())
                .isInstanceOf(ClassCastException.class);

        verify(analyticsRepository, times(1)).findEmployeeCountByDepartmentAndRole();
    }

    @Test
    @DisplayName("Should handle multiple departments with same roles")
    void getEmployeeCountByRoleAndOfficeLocation_MultipleDepartments() {
        // Given
        List<Object[]> multipleDeptData = Arrays.asList(
                new Object[]{"IT", Role.EMPLOYEE, 25L},
                new Object[]{"IT", Role.TEAM_LEAD, 5L},
                new Object[]{"HR", Role.EMPLOYEE, 10L},
                new Object[]{"HR", Role.HR_MANAGER, 2L}
        );
        when(analyticsRepository.findEmployeeCountByDepartmentAndRole()).thenReturn(multipleDeptData);

        // When
        Map<String, Map<String, Long>> result = employeeDetailsService.getEmployeeCountByRoleAndOfficeLocation();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get("IT")).hasSize(2);
        assertThat(result.get("IT")).containsEntry("EMPLOYEE", 25L);
        assertThat(result.get("IT")).containsEntry("TEAM_LEAD", 5L);
        assertThat(result.get("HR")).hasSize(2);
        assertThat(result.get("HR")).containsEntry("EMPLOYEE", 10L);
        assertThat(result.get("HR")).containsEntry("HR_MANAGER", 2L);

        verify(analyticsRepository, times(1)).findEmployeeCountByDepartmentAndRole();
    }
}