package com.nexora.backend.model.service.impl;

import com.nexora.backend.authentication.repository.EmployeeDetailsRepository;
import com.nexora.backend.domain.entity.EmployeeDetails;
import com.nexora.backend.domain.response.dto.PredictionResponse;
import com.nexora.backend.model.service.ModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    @NonNull
    private final EmployeeDetailsRepository employeeDetailsRepository;

    @NonNull
    private final WebClient webClient;

    @Override
    public Mono<PredictionResponse> getPredictionForEmployee(Integer userId) {
        Optional<EmployeeDetails> employeeDetailsOpt = employeeDetailsRepository.findByUserId(userId);

        if (employeeDetailsOpt.isEmpty()) {
            return Mono.error(new RuntimeException("Employee not found with user ID: " + userId));
        }

        EmployeeDetails employeeDetails = employeeDetailsOpt.get();

        // Build request body using the updated EmployeeDetails structure
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("Age", employeeDetails.getAge() != null ? employeeDetails.getAge() : 30);
        requestBody.put("BusinessTravel", mapBusinessTravel(employeeDetails.getBusinessTravel()));
        requestBody.put("DailyRate", employeeDetails.getDailyRate() != null ? employeeDetails.getDailyRate() : 800);
        requestBody.put("Department", mapDepartment(employeeDetails.getDepartment()));
        requestBody.put("DistanceFromHome", employeeDetails.getDistanceFromHome());
        requestBody.put("Education", employeeDetails.getEducation());
        requestBody.put("EducationField", mapEducationField(employeeDetails.getEducationField()));
        requestBody.put("EnvironmentSatisfaction", employeeDetails.getEnvironmentSatisfaction());
        requestBody.put("Gender", mapGender(employeeDetails.getGender()));
        requestBody.put("HourlyRate", employeeDetails.getHourlyRate());
        requestBody.put("JobInvolvement", employeeDetails.getJobInvolvement());
        requestBody.put("JobLevel", employeeDetails.getJobLevel());
        requestBody.put("JobRole", mapJobRole(employeeDetails.getJobRole()));
        requestBody.put("JobSatisfaction", employeeDetails.getJobSatisfaction());
        requestBody.put("MaritalStatus", mapMaritalStatus(employeeDetails.getMaritalStatus()));
        requestBody.put("MonthlyIncome", employeeDetails.getMonthlyIncome());
        requestBody.put("MonthlyRate", employeeDetails.getMonthlyRate());
        requestBody.put("NumCompaniesWorked", employeeDetails.getNumCompaniesWorked());
        requestBody.put("OverTime", mapOverTime(employeeDetails.getOverTime()));
        requestBody.put("RelationshipSatisfaction", employeeDetails.getRelationshipSatisfaction());
        requestBody.put("StockOptionLevel", employeeDetails.getStockOptionLevel());
        requestBody.put("TotalWorkingYears", employeeDetails.getTotalWorkingYears());
        requestBody.put("TrainingTimesLastYear", employeeDetails.getTrainingTimesLastYear());
        requestBody.put("WorkLifeBalance", employeeDetails.getWorkLifeBalance());
        requestBody.put("YearsAtCompany", employeeDetails.getYearsAtCompany());
        requestBody.put("YearsInCurrentRole", employeeDetails.getYearsInCurrentRole());
        requestBody.put("YearsSinceLastPromotion", employeeDetails.getYearsSinceLastPromotion());
        requestBody.put("YearsWithCurrManager", employeeDetails.getYearsWithCurrManager());

        // Call ML API using WebClient
        return webClient.post()
                .uri("http://localhost:8000/hr/predict")
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(PredictionResponse.class)
                .doOnNext(resp -> {
                    // Set employee name in response
                    resp.setEmployeeName(employeeDetails.getEmployeeName());
                    log.info("Prediction Response for {}: {}", employeeDetails.getEmployeeName(), resp);
                })
                .doOnError(error -> log.error("Error calling ML API for user {}: {}", userId, error.getMessage()));
    }

    // Mapping methods to convert string values to numeric codes expected by ML model
    private int mapBusinessTravel(String businessTravel) {
        if (businessTravel == null) return 1;
        return switch (businessTravel.toLowerCase()) {
            case "non-travel" -> 0;
            case "travel_rarely" -> 1;
            case "travel_frequently" -> 2;
            default -> 1;
        };
    }

    private int mapDepartment(String department) {
        if (department == null) return 5;
        return switch (department.toLowerCase()) {
            case "human resources", "hr" -> 0;
            case "research & development", "r&d", "research and development" -> 1;
            case "sales" -> 2;
            case "it", "information technology" -> 3;
            case "marketing" -> 4;
            case "finance", "accounting" -> 5;
            default -> 5;
        };
    }

    private int mapEducationField(String educationField) {
        if (educationField == null) return 2;
        return switch (educationField.toLowerCase()) {
            case "life sciences" -> 0;
            case "medical" -> 1;
            case "marketing" -> 2;
            case "technical degree", "technical" -> 3;
            case "human resources", "hr" -> 4;
            case "other" -> 5;
            default -> 2;
        };
    }

    private int mapGender(String gender) {
        if (gender == null) return 1;
        return gender.toLowerCase().equals("female") ? 0 : 1;
    }

    private int mapJobRole(String jobRole) {
        if (jobRole == null) return 5;
        return switch (jobRole.toLowerCase()) {
            case "sales executive" -> 0;
            case "research scientist" -> 1;
            case "laboratory technician" -> 2;
            case "manufacturing director" -> 3;
            case "healthcare representative" -> 4;
            case "manager" -> 5;
            case "sales representative" -> 6;
            case "research director" -> 7;
            case "human resources" -> 8;
            default -> 5;
        };
    }

    private int mapMaritalStatus(String maritalStatus) {
        if (maritalStatus == null) return 1;
        return switch (maritalStatus.toLowerCase()) {
            case "divorced" -> 0;
            case "married" -> 1;
            case "single" -> 2;
            default -> 1;
        };
    }

    private int mapOverTime(String overTime) {
        if (overTime == null) return 0;
        return overTime.toLowerCase().equals("yes") ? 1 : 0;
    }
}