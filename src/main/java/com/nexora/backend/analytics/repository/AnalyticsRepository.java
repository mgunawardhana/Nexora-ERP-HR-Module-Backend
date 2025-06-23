package com.nexora.backend.analytics.repository;

import com.nexora.backend.domain.entity.EmployeeDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<EmployeeDetails, Long> {

    @Query("SELECT u.role, COUNT(e.id) as count " +
            "FROM EmployeeDetails e JOIN e.user u " +
            "GROUP BY u.role")
    List<Object[]> findEmployeeCountByRole();

}
