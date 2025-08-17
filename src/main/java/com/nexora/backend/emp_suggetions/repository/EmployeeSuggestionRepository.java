package com.nexora.backend.emp_suggetions.repository;

import com.nexora.backend.domain.entity.EmployeeSuggestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing EmployeeSuggestion entities.
 * Extends JpaRepository to provide CRUD operations and custom query methods.
 */
@Repository
public interface EmployeeSuggestionRepository extends JpaRepository<EmployeeSuggestion, Long> {

    /**
     * Finds an EmployeeSuggestion by employee code, first name, and last name.
     *
     * @param employeeCode the employee's unique code
     * @param firstName    the employee's first name
     * @param lastName     the employee's last name
     * @return an Optional containing the EmployeeSuggestion if found, or empty otherwise
     */
    Optional<EmployeeSuggestion> findByEmployeeCodeAndFirstNameAndLastName(String employeeCode, String firstName, String lastName);

    /**
     * Finds all EmployeeSuggestions by employee code.
     *
     * @param employeeCode the employee's unique code
     * @return a list of EmployeeSuggestions matching the employee code
     */
    List<EmployeeSuggestion> findByEmployeeCode(String employeeCode);

    /**
     * Finds all EmployeeSuggestions by department.
     *
     * @param department the department name
     * @return a list of EmployeeSuggestions matching the department
     */
    List<EmployeeSuggestion> findByDepartment(String department);

    /**
     * Finds EmployeeSuggestions saved within a specific date range, with pagination.
     *
     * @param startDate the start of the date range
     * @param endDate   the end of the date range
     * @param pageable  the pagination information
     * @return a Page of EmployeeSuggestions within the date range
     */
    Page<EmployeeSuggestion> findBySavedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Counts the number of EmployeeSuggestions created after a specific date.
     *
     * @param startDate the cutoff date
     * @return the count of EmployeeSuggestions created after the start date
     */
    @Query("SELECT COUNT(es) FROM EmployeeSuggestion es WHERE es.savedAt >= :startDate")
    Long countSuggestionsCreatedAfter(@Param("startDate") LocalDateTime startDate);

    /**
     * Finds EmployeeSuggestions where the employee code contains the given string, case-insensitively.
     *
     * @param employeeCode the partial employee code to search for
     * @return a list of EmployeeSuggestions matching the partial employee code
     */
    List<EmployeeSuggestion> findByEmployeeCodeContainingIgnoreCase(String employeeCode);

    /**
     * Finds EmployeeSuggestions where the department contains the given string, case-insensitively.
     *
     * @param department the partial department name to search for
     * @return a list of EmployeeSuggestions matching the partial department name
     */
    List<EmployeeSuggestion> findByDepartmentContainingIgnoreCase(String department);

    /**
     * Finds EmployeeSuggestions within a specific date range, ordered by savedAt in descending order.
     *
     * @param startDate the start of the date range
     * @param endDate   the end of the date range
     * @return a list of EmployeeSuggestions within the date range
     */
    @Query("SELECT e FROM EmployeeSuggestion e WHERE e.savedAt BETWEEN :startDate AND :endDate ORDER BY e.savedAt DESC")
    List<EmployeeSuggestion> findSuggestionsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Finds recent EmployeeSuggestions created after a specific date, ordered by savedAt in descending order.
     *
     * @param cutoffDate the cutoff date
     * @return a list of recent EmployeeSuggestions
     */
    @Query("SELECT e FROM EmployeeSuggestion e WHERE e.savedAt >= :cutoffDate ORDER BY e.savedAt DESC")
    List<EmployeeSuggestion> findRecentSuggestions(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Counts the number of EmployeeSuggestions in a specific department, case-insensitively.
     *
     * @param department the department name
     * @return the count of EmployeeSuggestions in the department
     */
    @Query("SELECT COUNT(e) FROM EmployeeSuggestion e WHERE LOWER(e.department) = LOWER(:department)")
    Long countSuggestionsByDepartment(@Param("department") String department);
}