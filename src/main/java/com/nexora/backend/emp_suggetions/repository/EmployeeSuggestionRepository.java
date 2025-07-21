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

@Repository
public interface EmployeeSuggestionRepository extends JpaRepository<EmployeeSuggestion, Long> {

    Optional<EmployeeSuggestion> findByEmployeeCodeAndFirstNameAndLastName(
            String employeeCode, String firstName, String lastName);

    List<EmployeeSuggestion> findByEmployeeCode(String employeeCode);

    List<EmployeeSuggestion> findByDepartment(String department);

    Page<EmployeeSuggestion> findByCreatedAtBetween(
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("SELECT COUNT(es) FROM EmployeeSuggestion es WHERE es.createdAt >= :startDate")
    Long countSuggestionsCreatedAfter(@Param("startDate") LocalDateTime startDate);
}