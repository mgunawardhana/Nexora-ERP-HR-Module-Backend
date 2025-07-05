package com.nexora.backend.analytics.repository;

import com.nexora.backend.domain.entity.EmployeeKpiRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeKpiRecordRepository extends JpaRepository<EmployeeKpiRecord, Long> {
    boolean existsByRecordMonthAndRecordYear(Integer recordMonth, Integer recordYear);
}