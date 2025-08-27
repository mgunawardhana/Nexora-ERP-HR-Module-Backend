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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            EmployeeDetails employeeDetails = EmployeeDetails.builder()
                    .user(savedUser)
                    .employeeName(registrationRequest.getFirstName() + " " + registrationRequest.getLastName())
                    .nationalId(registrationRequest.getNationalId())
                    .phoneNumber(registrationRequest.getPhoneNumber())
                    .employeeCode(registrationRequest.getEmployeeCode())
                    .department(registrationRequest.getDepartment())
                    .jobRole(registrationRequest.getDesignation())
                    .joinDate(registrationRequest.getJoinDate())
                    .monthlyIncome(registrationRequest.getCurrentSalary())
                    .emergencyContactName(registrationRequest.getEmergencyContactName())
                    .emergencyContactPhone(registrationRequest.getEmergencyContactPhone())
                    .dateOfBirth(registrationRequest.getDateOfBirth())
                    .bankAccountNumber(registrationRequest.getBankAccountNumber())
                    .bankName(registrationRequest.getBankName())
                    .taxId(registrationRequest.getTaxId())
                    .managerId(registrationRequest.getManagerId())
                    .teamSize(registrationRequest.getTeamSize())
                    .specialization(registrationRequest.getSpecialization())
                    .contractStartDate(registrationRequest.getContractStartDate())
                    .contractEndDate(registrationRequest.getContractEndDate())
                    .hourlyRate(registrationRequest.getHourlyRate())
                    .certifications(registrationRequest.getCertifications())
                    .educationField(registrationRequest.getEducationLevel())
                    .university(registrationRequest.getUniversity())
                    .graduationYear(registrationRequest.getGraduationYear())
                    .totalWorkingYears(registrationRequest.getPreviousExperienceYears())
                    .employmentStatus(registrationRequest.getEmploymentStatus())
                    .probationEndDate(registrationRequest.getProbationEndDate())
                    .shiftTimings(registrationRequest.getShiftTimings())
                    .accessLevel(registrationRequest.getAccessLevel())
                    .budgetAuthority(registrationRequest.getBudgetAuthority())
                    .salesTarget(registrationRequest.getSalesTarget())
                    .commissionRate(registrationRequest.getCommissionRate())
                    .internDurationMonths(registrationRequest.getInternDurationMonths())
                    .mentorId(registrationRequest.getMentorId())
                    .officeLocation(registrationRequest.getOfficeLocation())
                    .workMode(registrationRequest.getWorkMode())
                    .notes(registrationRequest.getNotes())
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
            userDetails.put("id", user.getId());
            userDetails.put("firstName", user.getFirstName());
            userDetails.put("lastName", user.getLastName());
            userDetails.put("email", user.getEmail());
            userDetails.put("role", user.getRole());
            userDetails.put("user_profile_pic", user.getUserProfilePic());

            employeeDetailsRepository.findByUser(user).ifPresent(employeeDetails -> {
                userDetails.put("employeeName", employeeDetails.getEmployeeName());
                userDetails.put("nationalId", employeeDetails.getNationalId());
                userDetails.put("phoneNumber", employeeDetails.getPhoneNumber());
                userDetails.put("employeeCode", employeeDetails.getEmployeeCode());
                userDetails.put("department", employeeDetails.getDepartment());
                userDetails.put("jobRole", employeeDetails.getJobRole());
                userDetails.put("joinDate", employeeDetails.getJoinDate());
                userDetails.put("monthlyIncome", employeeDetails.getMonthlyIncome());
                userDetails.put("emergencyContactName", employeeDetails.getEmergencyContactName());
                userDetails.put("emergencyContactPhone", employeeDetails.getEmergencyContactPhone());
                userDetails.put("dateOfBirth", employeeDetails.getDateOfBirth());
                userDetails.put("bankAccountNumber", employeeDetails.getBankAccountNumber());
                userDetails.put("bankName", employeeDetails.getBankName());
                userDetails.put("taxId", employeeDetails.getTaxId());
                userDetails.put("managerId", employeeDetails.getManagerId());
                userDetails.put("teamSize", employeeDetails.getTeamSize());
                userDetails.put("specialization", employeeDetails.getSpecialization());
                userDetails.put("contractStartDate", employeeDetails.getContractStartDate());
                userDetails.put("contractEndDate", employeeDetails.getContractEndDate());
                userDetails.put("hourlyRate", employeeDetails.getHourlyRate());
                userDetails.put("certifications", employeeDetails.getCertifications());
                userDetails.put("educationField", employeeDetails.getEducationField());
                userDetails.put("university", employeeDetails.getUniversity());
                userDetails.put("graduationYear", employeeDetails.getGraduationYear());
                userDetails.put("totalWorkingYears", employeeDetails.getTotalWorkingYears());
                userDetails.put("employmentStatus", employeeDetails.getEmploymentStatus());
                userDetails.put("probationEndDate", employeeDetails.getProbationEndDate());
                userDetails.put("shiftTimings", employeeDetails.getShiftTimings());
                userDetails.put("accessLevel", employeeDetails.getAccessLevel());
                userDetails.put("budgetAuthority", employeeDetails.getBudgetAuthority());
                userDetails.put("salesTarget", employeeDetails.getSalesTarget());
                userDetails.put("commissionRate", employeeDetails.getCommissionRate());
                userDetails.put("internDurationMonths", employeeDetails.getInternDurationMonths());
                userDetails.put("mentorId", employeeDetails.getMentorId());
                userDetails.put("officeLocation", employeeDetails.getOfficeLocation());
                userDetails.put("workMode", employeeDetails.getWorkMode());
                userDetails.put("notes", employeeDetails.getNotes());
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
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}