package com.nexora.backend.analytics.repository;

import com.nexora.backend.domain.entity.EmployeeDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<EmployeeDetails, Long> {

    /**
     * Get employee count grouped by role and employment status
     */
    @Query("SELECT u.role, e.employmentStatus, COUNT(e.id) as count " +
            "FROM EmployeeDetails e JOIN e.user u " +
            "GROUP BY u.role, e.employmentStatus")
    List<Object[]> findEmployeeCountByRoleAndEmploymentStatus();

    /**
     * Get department-wise employee count
     */
    @Query("SELECT e.department, COUNT(e.id) as count " +
            "FROM EmployeeDetails e " +
            "GROUP BY e.department " +
            "ORDER BY COUNT(e.id) DESC")
    List<Object[]> findEmployeeCountByDepartment();

    /**
     * Get age group distribution
     */
    @Query("SELECT " +
            "CASE " +
            "WHEN e.age < 25 THEN '18-24' " +
            "WHEN e.age < 35 THEN '25-34' " +
            "WHEN e.age < 45 THEN '35-44' " +
            "WHEN e.age < 55 THEN '45-54' " +
            "ELSE '55+' " +
            "END as ageGroup, " +
            "COUNT(e.id) as count " +
            "FROM EmployeeDetails e " +
            "GROUP BY " +
            "CASE " +
            "WHEN e.age < 25 THEN '18-24' " +
            "WHEN e.age < 35 THEN '25-34' " +
            "WHEN e.age < 45 THEN '35-44' " +
            "WHEN e.age < 55 THEN '45-54' " +
            "ELSE '55+' " +
            "END " +
            "ORDER BY COUNT(e.id) DESC")
    List<Object[]> findAgeGroupDistribution();

    /**
     * Get average monthly income by department
     */
    @Query("SELECT e.department, AVG(e.monthlyIncome) as avgIncome " +
            "FROM EmployeeDetails e " +
            "WHERE e.monthlyIncome > 0 " +
            "GROUP BY e.department " +
            "ORDER BY AVG(e.monthlyIncome) DESC")
    List<Object[]> findAverageIncomeByDepartment();

    /**
     * Get job satisfaction statistics
     */
    @Query("SELECT e.department, AVG(e.jobSatisfaction) as avgSatisfaction " +
            "FROM EmployeeDetails e " +
            "WHERE e.jobSatisfaction > 0 " +
            "GROUP BY e.department " +
            "ORDER BY AVG(e.jobSatisfaction) DESC")
    List<Object[]> findJobSatisfactionByDepartment();

    /**
     * Get work-life balance statistics
     */
    @Query("SELECT e.department, AVG(e.workLifeBalance) as avgWorkLifeBalance " +
            "FROM EmployeeDetails e " +
            "WHERE e.workLifeBalance > 0 " +
            "GROUP BY e.department " +
            "ORDER BY AVG(e.workLifeBalance) DESC")
    List<Object[]> findWorkLifeBalanceByDepartment();

    /**
     * Get gender distribution by department
     */
    @Query("SELECT e.department, e.gender, COUNT(e.id) as count " +
            "FROM EmployeeDetails e " +
            "GROUP BY e.department, e.gender " +
            "ORDER BY e.department, e.gender")
    List<Object[]> findGenderDistributionByDepartment();

    /**
     * Get employees with high attrition risk (low satisfaction scores)
     */
    @Query("SELECT e FROM EmployeeDetails e " +
            "WHERE (e.jobSatisfaction <= 2 OR e.environmentSatisfaction <= 2 OR e.workLifeBalance <= 2) " +
            "AND e.employmentStatus = 'ACTIVE' " +
            "ORDER BY (e.jobSatisfaction + e.environmentSatisfaction + e.workLifeBalance) ASC")
    List<EmployeeDetails> findHighAttritionRiskEmployees();

    /**
     * Get experience distribution
     */
    @Query("SELECT " +
            "CASE " +
            "WHEN e.totalWorkingYears < 2 THEN '0-1 years' " +
            "WHEN e.totalWorkingYears < 5 THEN '2-4 years' " +
            "WHEN e.totalWorkingYears < 10 THEN '5-9 years' " +
            "WHEN e.totalWorkingYears < 15 THEN '10-14 years' " +
            "ELSE '15+ years' " +
            "END as experienceGroup, " +
            "COUNT(e.id) as count " +
            "FROM EmployeeDetails e " +
            "GROUP BY " +
            "CASE " +
            "WHEN e.totalWorkingYears < 2 THEN '0-1 years' " +
            "WHEN e.totalWorkingYears < 5 THEN '2-4 years' " +
            "WHEN e.totalWorkingYears < 10 THEN '5-9 years' " +
            "WHEN e.totalWorkingYears < 15 THEN '10-14 years' " +
            "ELSE '15+ years' " +
            "END " +
            "ORDER BY COUNT(e.id) DESC")
    List<Object[]> findExperienceDistribution();

    /**
     * Get overtime statistics by department
     */
    @Query("SELECT e.department, e.overTime, COUNT(e.id) as count " +
            "FROM EmployeeDetails e " +
            "GROUP BY e.department, e.overTime " +
            "ORDER BY e.department")
    List<Object[]> findOvertimeByDepartment();

    /**
     * Get employee details by user ID for analytics
     */
    @Query("SELECT e FROM EmployeeDetails e WHERE e.user.id = :userId")
    EmployeeDetails findByUserId(@Param("userId") Integer userId);

    /**
     * Get total employee count by employment status
     */
    @Query("SELECT e.employmentStatus, COUNT(e.id) as count " +
            "FROM EmployeeDetails e " +
            "GROUP BY e.employmentStatus " +
            "ORDER BY COUNT(e.id) DESC")
    List<Object[]> findEmployeeCountByEmploymentStatus();

    /**
     * Get training statistics
     */
    @Query("SELECT e.department, AVG(e.trainingTimesLastYear) as avgTraining " +
            "FROM EmployeeDetails e " +
            "WHERE e.trainingTimesLastYear >= 0 " +
            "GROUP BY e.department " +
            "ORDER BY AVG(e.trainingTimesLastYear) DESC")
    List<Object[]> findTrainingStatsByDepartment();

    /**
     * Get job level distribution
     */
    @Query("SELECT e.jobLevel, COUNT(e.id) as count " +
            "FROM EmployeeDetails e " +
            "GROUP BY e.jobLevel " +
            "ORDER BY e.jobLevel")
    List<Object[]> findJobLevelDistribution();

    /**
     * Get monthly income distribution
     */
    @Query("SELECT " +
            "CASE " +
            "WHEN e.monthlyIncome < 30000 THEN 'Below 30K' " +
            "WHEN e.monthlyIncome < 50000 THEN '30K-50K' " +
            "WHEN e.monthlyIncome < 80000 THEN '50K-80K' " +
            "WHEN e.monthlyIncome < 120000 THEN '80K-120K' " +
            "ELSE '120K+' " +
            "END as incomeGroup, " +
            "COUNT(e.id) as count " +
            "FROM EmployeeDetails e " +
            "WHERE e.monthlyIncome > 0 " +
            "GROUP BY " +
            "CASE " +
            "WHEN e.monthlyIncome < 30000 THEN 'Below 30K' " +
            "WHEN e.monthlyIncome < 50000 THEN '30K-50K' " +
            "WHEN e.monthlyIncome < 80000 THEN '50K-80K' " +
            "WHEN e.monthlyIncome < 120000 THEN '80K-120K' " +
            "ELSE '120K+' " +
            "END " +
            "ORDER BY COUNT(e.id) DESC")
    List<Object[]> findIncomeDistribution();
}