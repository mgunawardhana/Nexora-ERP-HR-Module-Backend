package com.nexora.backend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee_details")
public class EmployeeDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // JSON mapped fields
    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "age")
    private Integer age;

    @Column(name = "business_travel")
    private String businessTravel;

    @Column(name = "daily_rate", precision = 10, scale = 2)
    private BigDecimal dailyRate;

    @Column(name = "department")
    private String department;

    @Column(name = "distance_from_home")
    private Integer distanceFromHome;

    @Column(name = "education")
    private Integer education;

    @Column(name = "education_field")
    private String educationField;

    @Column(name = "environment_satisfaction")
    private Integer environmentSatisfaction;

    @Column(name = "gender")
    private String gender;

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "job_involvement")
    private Integer jobInvolvement;

    @Column(name = "job_level")
    private Integer jobLevel;

    @Column(name = "job_role")
    private String jobRole;

    @Column(name = "job_satisfaction")
    private Integer jobSatisfaction;

    @Column(name = "marital_status")
    private String maritalStatus;

    @Column(name = "monthly_income", precision = 12, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "monthly_rate", precision = 12, scale = 2)
    private BigDecimal monthlyRate;

    @Column(name = "num_companies_worked")
    private Integer numCompaniesWorked;

    @Column(name = "over_time")
    private String overTime;

    @Column(name = "relationship_satisfaction")
    private Integer relationshipSatisfaction;

    @Column(name = "stock_option_level")
    private Integer stockOptionLevel;

    @Column(name = "total_working_years")
    private Integer totalWorkingYears;

    @Column(name = "training_times_last_year")
    private Integer trainingTimesLastYear;

    @Column(name = "work_life_balance")
    private Integer workLifeBalance;

    @Column(name = "years_at_company")
    private Integer yearsAtCompany;

    @Column(name = "years_in_current_role")
    private Integer yearsInCurrentRole;

    @Column(name = "years_since_last_promotion")
    private Integer yearsSinceLastPromotion;

    @Column(name = "years_with_curr_manager")
    private Integer yearsWithCurrManager;

    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
