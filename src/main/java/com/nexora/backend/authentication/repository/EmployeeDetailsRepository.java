package com.nexora.backend.authentication.repository;

import com.nexora.backend.domain.entity.EmployeeDetails;
import com.nexora.backend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeDetailsRepository extends JpaRepository<EmployeeDetails, Integer> {
    Optional<EmployeeDetails> findByUser(User user);

    @Query("SELECT ed FROM EmployeeDetails ed WHERE ed.user.id = :userId")
    Optional<EmployeeDetails> findByUserId(@Param("userId") Integer userId);
}