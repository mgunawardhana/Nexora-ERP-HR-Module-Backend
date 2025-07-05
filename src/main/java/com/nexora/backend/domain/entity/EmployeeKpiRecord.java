package com.nexora.backend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "employee_kpi_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeKpiRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "monthly_kpi")
    private Double monthlyKpi;

    @Column(name = "total_actual_hours")
    private Double totalActualHours;

    @Column(name = "total_scheduled_hours")
    private Double totalScheduledHours;

    @Column(name = "workday_count")
    private Integer workdayCount;

    @Column(name = "kpi_percentage")
    private String kpiPercentage;

    @Column(name = "record_date")
    private LocalDate recordDate;

    @Column(name = "record_month")
    private Integer recordMonth;

    @Column(name = "record_year")
    private Integer recordYear;
}