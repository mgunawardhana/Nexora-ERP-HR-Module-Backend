package com.nexora.backend.authentication.repository;

import com.nexora.backend.domain.entity.EmployeeDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeDetailsRepository extends JpaRepository<EmployeeDetails, Integer> {
}
