package com.nexora.backend.authentication.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.backend.authentication.repository.EmployeeDetailsRepository;
import com.nexora.backend.authentication.repository.TokenRepository;
import com.nexora.backend.authentication.repository.UserRepository;
import com.nexora.backend.authentication.service.AuthenticationService;
import com.nexora.backend.domain.entity.EmployeeDetails;
import com.nexora.backend.domain.entity.Token;
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
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    @NonNull
    private final UserRepository userRepository;
    @NonNull
    private final TokenRepository tokenRepository;
    @NonNull
    private final EmployeeDetailsRepository employeeDetailsRepository;
    @NonNull
    private final PasswordEncoder passwordEncoder;
    @NonNull
    private final JwtServiceImpl jwtServiceImpl;
    @NonNull
    private final AuthenticationManager authenticationManager;
    @NonNull
    private final ResponseUtil responseUtil;

    @Override
    public ResponseEntity<APIResponse> findEmployeeByEmail(String email) {
        try {
            var user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                return responseUtil.wrapSuccess(user.get().getId(), HttpStatus.OK);
            }
            return responseUtil.wrapError("User not found", "No user found with email: " + email, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.warn("Failed to retrieve user ID by email {}: {}", email, e.getMessage());
            return responseUtil.wrapError("Failed to retrieve user ID", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
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

            // Create EmployeeDetails with the updated structure
            EmployeeDetails employeeDetails = EmployeeDetails.builder()
                    .user(savedUser)
                    .employeeName(registrationRequest.getFirstName() + " " + registrationRequest.getLastName())
                    .age(calculateAge(registrationRequest.getDateOfBirth()))
                    .businessTravel(getDefaultBusinessTravel())
                    .dailyRate(calculateDailyRate(registrationRequest.getCurrentSalary()))
                    .department(registrationRequest.getDepartment())
                    .distanceFromHome(getRandomDistanceFromHome())
                    .education(mapEducationLevel(registrationRequest.getEducationLevel()))
                    .educationField(registrationRequest.getEducationLevel())
                    .environmentSatisfaction(getDefaultSatisfactionLevel())
                    .gender(getDefaultGender())
                    .hourlyRate(registrationRequest.getHourlyRate() != null ?
                            registrationRequest.getHourlyRate().intValue() : calculateHourlyRate(registrationRequest.getCurrentSalary()))
                    .jobInvolvement(getDefaultJobInvolvement())
                    .jobLevel(mapJobLevel(registrationRequest.getDesignation()))
                    .jobRole(registrationRequest.getDesignation())
                    .jobSatisfaction(getDefaultSatisfactionLevel())
                    .maritalStatus(getDefaultMaritalStatus())
                    .monthlyIncome(registrationRequest.getCurrentSalary() != null ?
                            registrationRequest.getCurrentSalary().intValue() : getDefaultMonthlyIncome())
                    .monthlyRate(calculateMonthlyRate(registrationRequest.getCurrentSalary()))
                    .numCompaniesWorked(getRandomNumCompanies())
                    .overTime(getDefaultOverTime())
                    .relationshipSatisfaction(getDefaultSatisfactionLevel())
                    .stockOptionLevel(getDefaultStockOptionLevel())
                    .totalWorkingYears(registrationRequest.getPreviousExperienceYears() != null ?
                            registrationRequest.getPreviousExperienceYears() : getDefaultWorkingYears())
                    .trainingTimesLastYear(getDefaultTrainingTimes())
                    .workLifeBalance(getDefaultWorkLifeBalance())
                    .yearsAtCompany(calculateYearsAtCompany(registrationRequest.getJoinDate()))
                    .yearsInCurrentRole(getDefaultYearsInCurrentRole())
                    .yearsSinceLastPromotion(getDefaultYearsSinceLastPromotion())
                    .yearsWithCurrManager(getDefaultYearsWithCurrManager())
                    .employmentStatus(registrationRequest.getEmploymentStatus() != null ?
                            registrationRequest.getEmploymentStatus() : getDefaultEmploymentStatus())
                    .build();

            log.info("Saving employee details for user: {}", savedUser.getEmail());
            employeeDetailsRepository.save(employeeDetails);
            log.debug("Saved employee details for user ID: {}", savedUser.getId());

            String accessToken = jwtServiceImpl.generateToken(savedUser);
            String refreshToken = jwtServiceImpl.generateRefreshToken(savedUser);

            saveUserToken(savedUser, accessToken);

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
                    var accessToken = jwtServiceImpl.generateToken(user);
                    var refreshToken = jwtServiceImpl.generateRefreshToken(user);

                    revokeAllUserTokens(user);
                    saveUserToken(user, accessToken);

                    return AuthenticationResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .userName(user.getFirstName() + " " + user.getLastName())
                            .email(user.getEmail())
                            .role(String.valueOf(user.getRole()))
                            .build();
                }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return;
        }

        final String refreshToken = authorizationHeader.substring(7);
        final String userEmail = jwtServiceImpl.extractUserName(refreshToken);

        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail).orElseThrow();
            if (jwtServiceImpl.isTokenValidated(refreshToken, user)) {
                var accessToken = jwtServiceImpl.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        final String jwt = authHeader.substring(7);
        var storedToken = tokenRepository.findByToken(jwt).orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            SecurityContextHolder.clearContext();
        }
    }

    @Override
    public ResponseEntity<APIResponse> getAllAuthentications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepository.findAll(pageable);

        List<Map<String, Object>> userDetailsList = usersPage.getContent().stream().map(user -> {
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("userId", user.getId());
            userDetails.put("firstName", user.getFirstName());
            userDetails.put("lastName", user.getLastName());
            userDetails.put("email", user.getEmail());
            userDetails.put("role", user.getRole());
            userDetails.put("user_profile_pic", user.getUserProfilePic());

            employeeDetailsRepository.findByUser(user).ifPresent(employeeDetails -> {
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
                userDetails.put("createdAt", employeeDetails.getCreatedAt());
                userDetails.put("updatedAt", employeeDetails.getUpdatedAt());
            });
            return userDetails;
        }).toList();

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("content", userDetailsList);
        responseData.put("totalElements", usersPage.getTotalElements());
        responseData.put("totalPages", usersPage.getTotalPages());
        responseData.put("currentPage", usersPage.getNumber());
        responseData.put("pageSize", usersPage.getSize());
        responseData.put("numberOfElements", usersPage.getNumberOfElements());
        responseData.put("first", usersPage.isFirst());
        responseData.put("last", usersPage.isLast());
        responseData.put("empty", usersPage.isEmpty());

        return responseUtil.wrapSuccess(responseData, HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<APIResponse> deleteUser(Integer id) {
        try {
            var userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Delete associated tokens
                tokenRepository.deleteAllByUser(user);

                // Delete associated employee details
                employeeDetailsRepository.deleteByUser(user);

                // Delete the user
                userRepository.delete(user);

                return responseUtil.wrapSuccess("User deleted successfully", HttpStatus.OK);
            } else {
                return responseUtil.wrapError("User not found", "No user found with ID: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Failed to delete user with ID {}: {}", id, e.getMessage(), e);
            return responseUtil.wrapError("Failed to delete user", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<APIResponse> findEmployeeById(Integer id) {
        try {
            var employeeDetails = employeeDetailsRepository.findByUserId(id);
            if (employeeDetails.isPresent()) {
                return responseUtil.wrapSuccess(employeeDetails.get(), HttpStatus.OK);
            }
            return responseUtil.wrapError("Employee not found", "No employee found with user ID: " + id, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.warn("Failed to retrieve employee details by user ID {}: {}", id, e.getMessage());
            return responseUtil.wrapError("Failed to retrieve employee details", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(Integer.valueOf(user.getId()));
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    // Helper methods for mapping and default values
    private Integer calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) return 30; // Default age
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    private String getDefaultBusinessTravel() {
        String[] options = {"Travel_Rarely", "Travel_Frequently", "Non-Travel"};
        return options[new Random().nextInt(options.length)];
    }

    private Integer calculateDailyRate(java.math.BigDecimal salary) {
        if (salary == null) return 800; // Default daily rate
        return salary.divide(java.math.BigDecimal.valueOf(22), java.math.RoundingMode.HALF_UP).intValue();
    }

    private Integer getRandomDistanceFromHome() {
        return new Random().nextInt(30) + 1; // 1-30 km
    }

    private Integer mapEducationLevel(String educationLevel) {
        if (educationLevel == null) return 3;
        return switch (educationLevel.toLowerCase()) {
            case "bachelor", "bachelor's" -> 3;
            case "master", "master's" -> 4;
            case "phd", "doctorate" -> 5;
            case "diploma" -> 2;
            default -> 3;
        };
    }

    private Integer getDefaultSatisfactionLevel() {
        return new Random().nextInt(4) + 1; // 1-4 scale
    }

    private String getDefaultGender() {
        return new Random().nextBoolean() ? "Male" : "Female";
    }

    private Integer calculateHourlyRate(java.math.BigDecimal salary) {
        if (salary == null) return 50;
        return salary.divide(java.math.BigDecimal.valueOf(176), java.math.RoundingMode.HALF_UP).intValue(); // 22 days * 8 hours
    }

    private Integer getDefaultJobInvolvement() {
        return new Random().nextInt(4) + 1; // 1-4 scale
    }

    private Integer mapJobLevel(String designation) {
        if (designation == null) return 2;
        return switch (designation.toLowerCase()) {
            case "junior", "intern" -> 1;
            case "senior", "lead" -> 3;
            case "manager" -> 4;
            case "director", "vp" -> 5;
            default -> 2;
        };
    }

    private String getDefaultMaritalStatus() {
        String[] options = {"Single", "Married", "Divorced"};
        return options[new Random().nextInt(options.length)];
    }

    private Integer getDefaultMonthlyIncome() {
        return new Random().nextInt(10000) + 5000; // 5000-15000
    }

    private Integer calculateMonthlyRate(java.math.BigDecimal salary) {
        if (salary == null) return 15000;
        return salary.intValue();
    }

    private Integer getRandomNumCompanies() {
        return new Random().nextInt(5) + 1; // 1-5 companies
    }

    private String getDefaultOverTime() {
        return new Random().nextBoolean() ? "Yes" : "No";
    }

    private Integer getDefaultStockOptionLevel() {
        return new Random().nextInt(4); // 0-3
    }

    private Integer getDefaultWorkingYears() {
        return new Random().nextInt(20) + 1; // 1-20 years
    }

    private Integer getDefaultTrainingTimes() {
        return new Random().nextInt(6); // 0-5 times
    }

    private Integer getDefaultWorkLifeBalance() {
        return new Random().nextInt(4) + 1; // 1-4 scale
    }

    private Integer calculateYearsAtCompany(LocalDate joinDate) {
        if (joinDate == null) return 1;
        return Math.max(0, Period.between(joinDate, LocalDate.now()).getYears());
    }

    private Integer getDefaultYearsInCurrentRole() {
        return new Random().nextInt(5) + 1; // 1-5 years
    }

    private Integer getDefaultYearsSinceLastPromotion() {
        return new Random().nextInt(10); // 0-9 years
    }

    private Integer getDefaultYearsWithCurrManager() {
        return new Random().nextInt(8) + 1; // 1-8 years
    }

    private com.nexora.backend.domain.enums.EmploymentStatus getDefaultEmploymentStatus() {
        return com.nexora.backend.domain.enums.EmploymentStatus.ACTIVE;
    }
}