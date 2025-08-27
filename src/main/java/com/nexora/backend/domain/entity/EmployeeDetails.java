package com.nexora.backend.domain.entity;

import com.nexora.backend.domain.enums.EmploymentStatus;
import com.nexora.backend.domain.enums.WorkMode;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @OneToOne(fetch = FetchType.EAGER) // <-- THIS IS THE ONLY CHANGE
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "national_id")
    private String nationalId;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "employee_code")
    private String employeeCode;

    @Column(name = "department")
    private String department;

    @Column(name = "job_role")
    private String jobRole;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "monthly_income", precision = 12, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "tax_id")
    private String taxId;

    @Column(name = "manager_id")
    private Integer managerId;

    @Column(name = "team_size")
    private Integer teamSize;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "contract_start_date")
    private LocalDate contractStartDate;

    @Column(name = "contract_end_date")
    private LocalDate contractEndDate;

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "certifications")
    private String certifications;

    @Column(name = "education_field")
    private String educationField;

    @Column(name = "university")
    private String university;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Column(name = "total_working_years")
    private Integer totalWorkingYears;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status")
    private EmploymentStatus employmentStatus;

    @Column(name = "probation_end_date")
    private LocalDate probationEndDate;

    @Column(name = "shift_timings")
    private String shiftTimings;

    @Column(name = "access_level")
    private String accessLevel;

    @Column(name = "budget_authority", precision = 12, scale = 2)
    private BigDecimal budgetAuthority;

    @Column(name = "sales_target", precision = 12, scale = 2)
    private BigDecimal salesTarget;

    @Column(name = "commission_rate", precision = 10, scale = 2)
    private BigDecimal commissionRate;

    @Column(name = "intern_duration_months")
    private Integer internDurationMonths;

    @Column(name = "mentor_id")
    private Integer mentorId;

    @Column(name = "office_location")
    private String officeLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_mode")
    private WorkMode workMode;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

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