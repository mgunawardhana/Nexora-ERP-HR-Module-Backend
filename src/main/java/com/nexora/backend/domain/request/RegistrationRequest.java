package com.nexora.backend.domain.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexora.backend.domain.enums.EmploymentStatus;
import com.nexora.backend.domain.enums.Role;
import com.nexora.backend.domain.enums.WorkMode;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistrationRequest {
    private Role role;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String address;
    private String nationalId;
    private String phoneNumber;
    private String employeeCode;
    private String department;
    private String designation;
    private LocalDate joinDate;
    private BigDecimal currentSalary;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private LocalDate dateOfBirth;
    private String bankAccountNumber;
    private String bankName;
    private String taxId;
    private Integer managerId;
    private Integer teamSize;
    private String specialization;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private BigDecimal hourlyRate;
    private String certifications;
    private String educationLevel;
    private String university;
    private Integer graduationYear;
    private Integer previousExperienceYears;
    private EmploymentStatus employmentStatus;
    private LocalDate probationEndDate;
    private String shiftTimings;
    private String accessLevel;
    private BigDecimal budgetAuthority;
    private BigDecimal salesTarget;
    private BigDecimal commissionRate;
    private Integer internDurationMonths;
    private Integer mentorId;
    private String officeLocation;
    private WorkMode workMode;
    private String notes;
}