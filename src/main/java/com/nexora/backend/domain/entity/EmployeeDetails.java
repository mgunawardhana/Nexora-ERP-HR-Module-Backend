package com.nexora.backend.domain.entity;

import com.nexora.backend.domain.enums.EmploymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter
@Table(name = "employee_details")
public class EmployeeDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "age")
    private Integer age;

    @Column(name = "business_travel")
    private String businessTravel;

    @Column(name = "daily_rate")
    private Integer dailyRate;

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

    @Column(name = "hourly_rate")
    private Integer hourlyRate;

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

    @Column(name = "monthly_income")
    private Integer monthlyIncome;

    @Column(name = "monthly_rate")
    private Integer monthlyRate;

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

    // Additional fields needed for analytics
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status")
    private EmploymentStatus employmentStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();

        // Set default employment status if not provided
        if (employmentStatus == null) {
            employmentStatus = EmploymentStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}