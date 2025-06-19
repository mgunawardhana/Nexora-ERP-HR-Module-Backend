package com.nexora.backend.authentication.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.backend.authentication.repository.EmployeeDetailsRepository;
import com.nexora.backend.authentication.repository.UserRepository;
import com.nexora.backend.authentication.service.AuthenticationService;
import com.nexora.backend.constant.SqlQuery;
import com.nexora.backend.domain.entity.EmployeeDetails;
import com.nexora.backend.domain.entity.User;
import com.nexora.backend.domain.enums.EmploymentStatus;
import com.nexora.backend.domain.enums.TokenType;
import com.nexora.backend.domain.enums.WorkMode;
import com.nexora.backend.domain.request.AuthenticationRequest;
import com.nexora.backend.domain.request.RegistrationRequest;
import com.nexora.backend.domain.response.APIResponse;
import com.nexora.backend.domain.response.AuthenticationResponse;
import com.nexora.backend.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final JdbcTemplate writeJdbcTemplate;

    @NonNull
    private final JdbcTemplate readJdbcTemplate;

    @NonNull
    private final PasswordEncoder passwordEncoder;

    @NonNull
    private final JwtServiceImpl jwtServiceImpl;

    @NonNull
    private final AuthenticationManager authenticationManager;

    @NonNull
    private final EmployeeDetailsRepository employeeDetailsRepository;


    @NonNull
    private final ResponseUtil responseUtil;

    @Override
    public ResponseEntity<APIResponse> findDriverEmailByDriverId(String email) throws IOException {
        try {
            Integer driverId = readJdbcTemplate.queryForObject(
                    SqlQuery.SelectQuery.FIND_ID_BY_EMAIL,
                    new Object[]{email},
                    (rs, rowNum) -> rs.getInt("id")
            );
            return responseUtil.wrapSuccess(driverId, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("Failed to retrieve driver ID {}", e.getMessage());
            return responseUtil.wrapError("Failed to retrieve driver ID", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Registers a new user and their associated employee details into the system.
     * <p>
     * This method performs the following operations:
     * <ul>
     *     <li>Creates and saves a new {@link User} entity based on the provided {@link RegistrationRequest}.</li>
     *     <li>Initializes and saves an {@link EmployeeDetails} record tied to the saved user, filling defaults where applicable.</li>
     *     <li>Generates access and refresh JWT tokens for the user.</li>
     *     <li>Attempts to save the generated token to the database using {@code writeJdbcTemplate}.</li>
     * </ul>
     * <p>
     * All operations are wrapped in a transactional context to ensure atomicity. If any part of the process fails, the transaction will roll back.
     *
     * @param registrationRequest the registration request payload containing user and employee details.
     * @return an {@link AuthenticationResponse} containing access token, refresh token, username, and role information.
     * @throws RuntimeException if any part of the registration or token-saving process fails.
     */
    @Transactional
    public AuthenticationResponse register(RegistrationRequest registrationRequest) {
        try {
            User user = User.builder()
                    .firstName(registrationRequest.getFirstName())
                    .lastName(registrationRequest.getLastName())
                    .email(registrationRequest.getEmail())
                    .password(passwordEncoder.encode(registrationRequest.getPassword()))
                    .role(registrationRequest.getRole())
                    .build();

            log.info("Processing registration for user: {}", user.getEmail());

            User savedUser = userRepository.save(user);
            log.debug("Saved user with ID: {}", savedUser.getId());

            EmployeeDetails employeeDetails = EmployeeDetails.builder()
                    .user(savedUser)
                    .employeeCode(registrationRequest.getEmployeeCode() != null ? registrationRequest.getEmployeeCode() : "EMP" + System.currentTimeMillis())
                    .department(registrationRequest.getDepartment() != null ? registrationRequest.getDepartment() : "Unassigned")
                    .designation(registrationRequest.getDesignation() != null ? registrationRequest.getDesignation() : "New Hire")
                    .joinDate(registrationRequest.getJoinDate() != null ? registrationRequest.getJoinDate() : LocalDate.now())
                    .currentSalary(registrationRequest.getCurrentSalary() != null ? registrationRequest.getCurrentSalary() : new BigDecimal("0.01"))
                    .phoneNumber(registrationRequest.getPhoneNumber() != null ? registrationRequest.getPhoneNumber() : "N/A")
                    .address(registrationRequest.getAddress() != null ? registrationRequest.getAddress() : "N/A")
                    .emergencyContactName(registrationRequest.getEmergencyContactName() != null ? registrationRequest.getEmergencyContactName() : "N/A")
                    .emergencyContactPhone(registrationRequest.getEmergencyContactPhone() != null ? registrationRequest.getEmergencyContactPhone() : "N/A")
                    .dateOfBirth(registrationRequest.getDateOfBirth() != null ? registrationRequest.getDateOfBirth() : LocalDate.of(2000, 1, 1))
                    .nationalId(registrationRequest.getNationalId() != null ? registrationRequest.getNationalId() : "N/A")
                    .bankAccountNumber(registrationRequest.getBankAccountNumber() != null ? registrationRequest.getBankAccountNumber() : "N/A")
                    .bankName(registrationRequest.getBankName() != null ? registrationRequest.getBankName() : "N/A")
                    .taxId(registrationRequest.getTaxId() != null ? registrationRequest.getTaxId() : "N/A")
                    .managerId(registrationRequest.getManagerId() != null ? registrationRequest.getManagerId() : 0)
                    .teamSize(registrationRequest.getTeamSize() != null ? registrationRequest.getTeamSize() : 0)
                    .specialization(registrationRequest.getSpecialization() != null ? registrationRequest.getSpecialization() : "N/A")
                    .contractStartDate(registrationRequest.getContractStartDate() != null ? registrationRequest.getContractStartDate() : LocalDate.now())
                    .contractEndDate(registrationRequest.getContractEndDate() != null ? registrationRequest.getContractEndDate() : LocalDate.now().plusYears(1))
                    .hourlyRate(registrationRequest.getHourlyRate() != null ? registrationRequest.getHourlyRate() : new BigDecimal("0.00"))
                    .certifications(registrationRequest.getCertifications() != null ? registrationRequest.getCertifications() : "None")
                    .educationLevel(registrationRequest.getEducationLevel() != null ? registrationRequest.getEducationLevel() : "N/A")
                    .university(registrationRequest.getUniversity() != null ? registrationRequest.getUniversity() : "N/A")
                    .graduationYear(registrationRequest.getGraduationYear() != null ? registrationRequest.getGraduationYear() : 0)
                    .previousExperienceYears(registrationRequest.getPreviousExperienceYears() != null ? registrationRequest.getPreviousExperienceYears() : 0)
                    .employmentStatus(registrationRequest.getEmploymentStatus() != null ? registrationRequest.getEmploymentStatus() : EmploymentStatus.PROBATION)
                    .probationEndDate(registrationRequest.getProbationEndDate() != null ? registrationRequest.getProbationEndDate() : LocalDate.now().plusMonths(3))
                    .shiftTimings(registrationRequest.getShiftTimings() != null ? registrationRequest.getShiftTimings() : "9:00-17:00")
                    .accessLevel(registrationRequest.getAccessLevel() != null ? registrationRequest.getAccessLevel() : "BASIC")
                    .budgetAuthority(registrationRequest.getBudgetAuthority() != null ? registrationRequest.getBudgetAuthority() : new BigDecimal("0.00"))
                    .salesTarget(registrationRequest.getSalesTarget() != null ? registrationRequest.getSalesTarget() : new BigDecimal("0.00"))
                    .commissionRate(registrationRequest.getCommissionRate() != null ? registrationRequest.getCommissionRate() : new BigDecimal("0.00"))
                    .internDurationMonths(registrationRequest.getInternDurationMonths() != null ? registrationRequest.getInternDurationMonths() : 0)
                    .mentorId(registrationRequest.getMentorId() != null ? registrationRequest.getMentorId() : 0)
                    .officeLocation(registrationRequest.getOfficeLocation() != null ? registrationRequest.getOfficeLocation() : "Main Office")
                    .workMode(registrationRequest.getWorkMode() != null ? registrationRequest.getWorkMode() : WorkMode.OFFICE)
                    .notes(registrationRequest.getNotes() != null ? registrationRequest.getNotes() : "")
                    .build();

            log.info("Saving employee details for user: {}", savedUser.getEmail());

            employeeDetailsRepository.save(employeeDetails);
            log.debug("Saved employee details for user ID: {}", savedUser.getId());

            // Generating tokens
            String accessToken = jwtServiceImpl.generateToken(savedUser);
            String refreshToken = jwtServiceImpl.generateRefreshToken(savedUser);

            try {
                log.debug("Attempting to save token for user ID: {} with token: {}", savedUser.getId(), accessToken);
                writeJdbcTemplate.update(
                        SqlQuery.InsertQuery.INSERT_TOKEN,
                        accessToken,
                        TokenType.BEARER.name(),
                        false,
                        false,
                        savedUser.getId()
                );
                log.info("Token saved successfully for user: {}", savedUser.getEmail());
            } catch (Exception e) {
                log.error("Failed to save token for user {}: {}", savedUser.getEmail(), e.getMessage(), e);
                if (e.getCause() instanceof SQLException sqlEx) {
                    log.error("SQL Error Code: {}, SQL State: {}", sqlEx.getErrorCode(), sqlEx.getSQLState());
                }
                throw new RuntimeException("Failed to save authentication token: " + e.getMessage(), e);
            }

            return AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userName(savedUser.getFirstName())
                    .role(String.valueOf(savedUser.getRole()))
                    .build();

        } catch (Exception e) {
            log.error("Registration failed for user {}: {}", registrationRequest.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));

        log.info("AuthenticationResponse From Authenticate Function: {}", user);

        var accessToken = jwtServiceImpl.generateToken(user);
        var refreshToken = jwtServiceImpl.generateRefreshToken(user);

        try {
            int updatedRows = writeJdbcTemplate.update(SqlQuery.InsertQuery.INVOKE_REVOKE_ALL_USER_TOKENS, accessToken, Boolean.FALSE, Boolean.FALSE, user.getId());

            if (updatedRows == 0) {
                writeJdbcTemplate.update(SqlQuery.InsertQuery.INSERT_TOKEN, accessToken, TokenType.BEARER.name(), Boolean.FALSE, Boolean.FALSE, user.getId());
            }
            log.info("Token updated/saved successfully for user: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error managing token for user {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Failed to manage authentication token", e);
        }

        log.info("Generated Token from Authenticate Function: {}", accessToken);

        return AuthenticationResponse.builder().accessToken(Objects.requireNonNull(accessToken)).refreshToken(Objects.requireNonNull(refreshToken)).userName(user.getFirstName() + " " + user.getLastName()).role(String.valueOf(user.getRole())).build();
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authorizationHeader = request.getHeader(Objects.requireNonNull(HttpHeaders.AUTHORIZATION, "Authorization header cannot be null"));
        final String refreshToken;
        final String userEmail;

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.error("Authorization Header is Null or Not Started with Bearer");
            return;
        }

        refreshToken = authorizationHeader.substring(7);
        userEmail = jwtServiceImpl.extractUserName(refreshToken);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = this.userRepository.findByEmail(userEmail).orElseThrow();
            log.info("User Details from Refresh Token: {}", userDetails);

            if (jwtServiceImpl.isTokenValidated(refreshToken, userDetails)) {
                var accessToken = jwtServiceImpl.generateToken(userDetails);
                log.info("Generated Token from Refresh Token Function: {}", accessToken);

                try {
                    writeJdbcTemplate.update(SqlQuery.InsertQuery.INSERT_TOKEN, accessToken, TokenType.BEARER.name(), Boolean.FALSE, Boolean.FALSE, userDetails.getId());
                    log.info("New access token saved successfully for user: {}", userDetails.getEmail());
                } catch (Exception e) {
                    log.error("Error saving new access token for user {}: {}", userDetails.getEmail(), e.getMessage());
                }

                var authResponse = AuthenticationResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
                log.info("Authentication Response from Refresh Token Function: {}", authResponse);

                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        final String jwt = authHeader.substring(7);
        var userEmail = jwtServiceImpl.extractUserName(jwt);

        if (userEmail != null) {
            var user = userRepository.findByEmail(userEmail).orElse(null);
            if (user != null) {
                try {
                    writeJdbcTemplate.update(SqlQuery.UpdateQuery.REVOKE_ALL_USER_TOKENS, Boolean.TRUE, Boolean.TRUE, user.getId());
                    log.info("Successfully logged out user: {}", userEmail);
                } catch (Exception e) {
                    log.error("Error during logout for user {}: {}", userEmail, e.getMessage());
                }
            }
        }
    }

    @Override
    public ResponseEntity<APIResponse> getAllAuthentications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepository.findAll(pageable);

        List<Map<String, Object>> userDetailsList = usersPage.getContent().stream().map(user -> {
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("id", user.getId());
            userDetails.put("firstName", user.getFirstName());
            userDetails.put("lastName", user.getLastName());
            userDetails.put("email", user.getEmail());
            userDetails.put("role", user.getRole());
            userDetails.put("user_profile_pic", user.getUserProfilePic());
            userDetails.put("password", passwordEncoder.encode(user.getPassword()));
            return userDetails;
        }).toList();

        return responseUtil.wrapSuccess(userDetailsList, HttpStatus.OK);
    }
}