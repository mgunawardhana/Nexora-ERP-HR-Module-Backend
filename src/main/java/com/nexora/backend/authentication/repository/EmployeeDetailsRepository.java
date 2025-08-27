package com.nexora.backend.authentication.repository;

import com.nexora.backend.domain.entity.EmployeeDetails;
import com.nexora.backend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeDetailsRepository extends JpaRepository<EmployeeDetails, Long> {

    Optional<EmployeeDetails> findByUser(User user);

    Optional<EmployeeDetails> findByUserId(Integer userId);
}