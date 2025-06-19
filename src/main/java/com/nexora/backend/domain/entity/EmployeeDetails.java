package com.nexora.backend.domain.entity;

import com.nexora.backend.domain.enums.EmploymentStatus;
import com.nexora.backend.domain.enums.WorkMode;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Column(name = "employee_code", unique = true)
    private String employeeCode = "EMP" + System.currentTimeMillis();

    @Column(name = "department")
    private String department = "Unassigned";

    @Column(name = "designation")
    private String designation = "New Hire";

    @Column(name = "join_date")
    private LocalDate joinDate = LocalDate.now();

    @Column(name = "current_salary", precision = 12, scale = 2)
    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than 0")
    private BigDecimal currentSalary = new BigDecimal("0.01");

    @Column(name = "phone_number")
    private String phoneNumber = "N/A";

    @Column(name = "address", columnDefinition = "TEXT")
    private String address = "N/A";

    @Column(name = "emergency_contact_name")
    private String emergencyContactName = "N/A";

    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone = "N/A";

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth = LocalDate.of(2000, 1, 1);

    @Column(name = "national_id")
    private String nationalId = "N/A";

    @Column(name = "bank_account_number")
    private String bankAccountNumber = "N/A";

    @Column(name = "bank_name")
    private String bankName = "N/A";

    @Column(name = "tax_id")
    private String taxId = "N/A";

    @Column(name = "manager_id")
    private Integer managerId = 0;

    @Column(name = "team_size")
    private Integer teamSize = 0;

    @Column(name = "specialization")
    private String specialization = "N/A";

    @Column(name = "contract_start_date")
    private LocalDate contractStartDate = LocalDate.now();

    @Column(name = "contract_end_date")
    private LocalDate contractEndDate = LocalDate.now().plusYears(1);

    @Column(name = "hourly_rate", precision = 8, scale = 2)
    private BigDecimal hourlyRate = new BigDecimal("0.00");

    @Column(name = "certifications", columnDefinition = "TEXT")
    private String certifications = "None";

    @Column(name = "education_level")
    private String educationLevel = "N/A";

    @Column(name = "university")
    private String university = "N/A";

    @Column(name = "graduation_year")
    private Integer graduationYear = 0;

    @Column(name = "previous_experience_years")
    private Integer previousExperienceYears = 0;

    @Column(name = "employment_status")
    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus = EmploymentStatus.PROBATION;

    @Column(name = "probation_end_date")
    private LocalDate probationEndDate = LocalDate.now().plusMonths(3);

    @Column(name = "shift_timings")
    private String shiftTimings = "9:00-17:00";

    @Column(name = "access_level")
    private String accessLevel = "BASIC";

    @Column(name = "budget_authority", precision = 12, scale = 2)
    private BigDecimal budgetAuthority = new BigDecimal("0.00");

    @Column(name = "sales_target", precision = 12, scale = 2)
    private BigDecimal salesTarget = new BigDecimal("0.00");

    @Column(name = "commission_rate", precision = 5, scale = 2)
    private BigDecimal commissionRate = new BigDecimal("0.00");

    @Column(name = "intern_duration_months")
    private Integer internDurationMonths = 0;

    @Column(name = "mentor_id")
    private Integer mentorId = 0;

    @Column(name = "office_location")
    private String officeLocation = "Main Office";

    @Column(name = "work_mode")
    @Enumerated(EnumType.STRING)
    private WorkMode workMode = WorkMode.OFFICE;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes = "";

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