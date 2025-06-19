package com.nexora.backend.attendence.repository;

import com.nexora.backend.domain.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}
