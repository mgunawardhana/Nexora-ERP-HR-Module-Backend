package com.nexora.backend.analytics.repository;

import com.nexora.backend.domain.entity.EmployeeDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<EmployeeDetails, Long> {

    @Query("SELECT u.role, e.employmentStatus, COUNT(e.id) as count " +
            "FROM EmployeeDetails e JOIN e.user u " +
            "GROUP BY u.role, e.employmentStatus")
    List<Object[]> findEmployeeCountByRoleAndEmploymentStatus();

    @Query("SELECT u.role, COUNT(e.id) as count " +
            "FROM EmployeeDetails e JOIN e.user u " +
            "GROUP BY u.role")
    List<Object[]> findEmployeeCountByRole();

    @Query("SELECT u.role, e.officeLocation, COUNT(e.id) as count " +
            "FROM EmployeeDetails e JOIN e.user u " +
            "GROUP BY u.role, e.officeLocation")
    List<Object[]> findEmployeeCountByRoleAndOfficeLocation();

    @Query("SELECT u.id, u.firstName, u.lastName, u.role, " +
            "e.department, e.joinDate, e.currentSalary, e.hourlyRate, " +
            "e.educationLevel, e.previousExperienceYears, " +
            "a.attendanceDate, a.status, a.checkInTime, a.lunchOutTime, " +
            "a.lunchInTime, a.checkOutTime, a.dailyWorkingHours " +
            "FROM EmployeeDetails e " +
            "JOIN e.user u " +
            "LEFT JOIN Attendance a ON a.user.id = u.id " +
            "ORDER BY u.id, a.attendanceDate DESC")
    List<Object[]> findEmployeeAttendanceData();

    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO employee_kpi_records (employee_name, monthly_kpi, total_actual_hours, " +
            "total_scheduled_hours, workday_count, kpi_percentage, record_date, record_month, record_year) " +
            "VALUES (:employeeName, :monthlyKpi, :totalActualHours, :totalScheduledHours, :workdayCount, :kpiPercentage, " +
            ":recordDate, :recordMonth, :recordYear)")
    void saveEmployeeKpiRecord(
            String employeeName,
            Double monthlyKpi,
            Double totalActualHours,
            Double totalScheduledHours,
            Integer workdayCount,
            String kpiPercentage,
            LocalDate recordDate,
            Integer recordMonth,
            Integer recordYear
    );

}
