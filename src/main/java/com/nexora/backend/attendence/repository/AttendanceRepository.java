package com.nexora.backend.attendence.repository;

import com.nexora.backend.domain.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query("SELECT a FROM Attendance a WHERE a.user.id = :userId AND a.attendanceDate = :attendanceDate")
    Optional<Attendance> findByUserIdAndAttendanceDate(@Param("userId") Long userId,
                                                       @Param("attendanceDate") LocalDate attendanceDate);

    @Query("SELECT a FROM Attendance a WHERE a.user.id = :userId ORDER BY a.attendanceDate DESC")
    List<Attendance> findByUserId(@Param("userId") Long userId);

    @Query("SELECT a FROM Attendance a WHERE a.user.id = :userId AND a.attendanceDate BETWEEN :startDate AND :endDate ORDER BY a.attendanceDate")
    List<Attendance> findByUserIdAndDateRange(@Param("userId") Long userId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    List<Attendance> findByStatus(String status);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.id = :userId AND a.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId,
                                @Param("status") String status);
}