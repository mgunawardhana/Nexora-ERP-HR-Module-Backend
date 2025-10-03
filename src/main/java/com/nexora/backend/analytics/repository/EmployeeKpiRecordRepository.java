package com.nexora.backend.analytics.repository;

import com.nexora.backend.domain.entity.EmployeeKpiRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeKpiRecordRepository extends JpaRepository<EmployeeKpiRecord, Long> {
    boolean existsByRecordMonthAndRecordYear(Integer recordMonth, Integer recordYear);

    List<EmployeeKpiRecord> findTop5ByOrderByMonthlyKpiDesc();
}