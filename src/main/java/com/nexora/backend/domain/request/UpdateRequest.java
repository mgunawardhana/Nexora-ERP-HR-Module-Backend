package com.nexora.backend.domain.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexora.backend.domain.enums.EmploymentStatus;
import com.nexora.backend.domain.enums.Role;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private String address;
    private String nationalId;
    private String phoneNumber;
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
    private EmploymentStatus employmentStatus;
}