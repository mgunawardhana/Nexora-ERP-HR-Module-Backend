package com.nexora.backend.authentication.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.backend.authentication.repository.EmployeeDetailsRepository;
import com.nexora.backend.authentication.repository.UserRepository;
import com.nexora.backend.authentication.service.AuthenticationService;
import com.nexora.backend.constant.SqlQuery;
import com.nexora.backend.domain.entity.EmployeeDetails;
import com.nexora.backend.domain.entity.User;
import com.nexora.backend.domain.enums.TokenType;
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
    public ResponseEntity<APIResponse> findEmployeeByEmail(String email) throws IOException {
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
                    .employeeName(registrationRequest.getFirstName() + " " + registrationRequest.getLastName())
                    .department(registrationRequest.getDepartment())
                    .age(null)
                    .businessTravel(null)
                    .dailyRate(null)
                    .distanceFromHome(null)
                    .education(null)
                    .educationField(registrationRequest.getEducationLevel())
                    .environmentSatisfaction(null)
                    .gender(null)
                    .hourlyRate(registrationRequest.getHourlyRate())
                    .jobInvolvement(null)
                    .jobLevel(null)
                    .jobRole(null)
                    .jobSatisfaction(null)
                    .maritalStatus(null)
                    .monthlyIncome(null)
                    .monthlyRate(null)
                    .numCompaniesWorked(null)
                    .overTime(null)
                    .relationshipSatisfaction(null)
                    .stockOptionLevel(null)
                    .totalWorkingYears(registrationRequest.getPreviousExperienceYears())
                    .trainingTimesLastYear(null)
                    .workLifeBalance(null)
                    .yearsAtCompany(null)
                    .yearsInCurrentRole(null)
                    .yearsSinceLastPromotion(null)
                    .yearsWithCurrManager(null)
                    .employmentStatus(registrationRequest.getEmploymentStatus())
                    .officeLocation(registrationRequest.getOfficeLocation())
                    .build();

            log.info("Saving employee details for user: {}", savedUser.getEmail());

            employeeDetailsRepository.save(employeeDetails);
            log.debug("Saved employee details for user ID: {}", savedUser.getId());

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

            EmployeeDetails employeeDetails = employeeDetailsRepository.findByUser(user).orElse(null);

            if (employeeDetails != null) {
                userDetails.put("employeeName", employeeDetails.getEmployeeName());
                userDetails.put("age", employeeDetails.getAge());
                userDetails.put("businessTravel", employeeDetails.getBusinessTravel());
                userDetails.put("dailyRate", employeeDetails.getDailyRate());
                userDetails.put("department", employeeDetails.getDepartment());
                userDetails.put("distanceFromHome", employeeDetails.getDistanceFromHome());
                userDetails.put("education", employeeDetails.getEducation());
                userDetails.put("educationField", employeeDetails.getEducationField());
                userDetails.put("environmentSatisfaction", employeeDetails.getEnvironmentSatisfaction());
                userDetails.put("gender", employeeDetails.getGender());
                userDetails.put("hourlyRate", employeeDetails.getHourlyRate());
                userDetails.put("jobInvolvement", employeeDetails.getJobInvolvement());
                userDetails.put("jobLevel", employeeDetails.getJobLevel());
                userDetails.put("jobRole", employeeDetails.getJobRole());
                userDetails.put("jobSatisfaction", employeeDetails.getJobSatisfaction());
                userDetails.put("maritalStatus", employeeDetails.getMaritalStatus());
                userDetails.put("monthlyIncome", employeeDetails.getMonthlyIncome());
                userDetails.put("monthlyRate", employeeDetails.getMonthlyRate());
                userDetails.put("numCompaniesWorked", employeeDetails.getNumCompaniesWorked());
                userDetails.put("overTime", employeeDetails.getOverTime());
                userDetails.put("relationshipSatisfaction", employeeDetails.getRelationshipSatisfaction());
                userDetails.put("stockOptionLevel", employeeDetails.getStockOptionLevel());
                userDetails.put("totalWorkingYears", employeeDetails.getTotalWorkingYears());
                userDetails.put("trainingTimesLastYear", employeeDetails.getTrainingTimesLastYear());
                userDetails.put("workLifeBalance", employeeDetails.getWorkLifeBalance());
                userDetails.put("yearsAtCompany", employeeDetails.getYearsAtCompany());
                userDetails.put("yearsInCurrentRole", employeeDetails.getYearsInCurrentRole());
                userDetails.put("yearsSinceLastPromotion", employeeDetails.getYearsSinceLastPromotion());
                userDetails.put("yearsWithCurrManager", employeeDetails.getYearsWithCurrManager());
                userDetails.put("employmentStatus", employeeDetails.getEmploymentStatus());
                userDetails.put("officeLocation", employeeDetails.getOfficeLocation());
                userDetails.put("createdAt", employeeDetails.getCreatedAt());
                userDetails.put("updatedAt", employeeDetails.getUpdatedAt());
            }
            return userDetails;
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("content", userDetailsList);
        response.put("totalElements", usersPage.getTotalElements());
        response.put("totalPages", usersPage.getTotalPages());
        response.put("currentPage", usersPage.getNumber());
        response.put("pageSize", usersPage.getSize());
        response.put("numberOfElements", usersPage.getNumberOfElements());
        response.put("first", usersPage.isFirst());
        response.put("last", usersPage.isLast());
        response.put("empty", usersPage.isEmpty());

        return responseUtil.wrapSuccess(response, HttpStatus.OK);
    }
}