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

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("employee_name", "John Doe");
        requestBody.put("Age", 35);
        requestBody.put("BusinessTravel", "Travel_Rarely");
        requestBody.put("DailyRate", 800);
        requestBody.put("Department", "Sales");
        requestBody.put("DistanceFromHome", 10);
        requestBody.put("Education", 3);
        requestBody.put("EducationField", "Life Sciences");
        requestBody.put("EnvironmentSatisfaction", 3);
        requestBody.put("Gender", "Male");
        requestBody.put("HourlyRate", 70);
        requestBody.put("JobInvolvement", 3);
        requestBody.put("JobLevel", 2);
        requestBody.put("JobRole", "Sales Executive");
        requestBody.put("JobSatisfaction", 4);
        requestBody.put("MaritalStatus", "Married");
        requestBody.put("MonthlyIncome", 5000);
        requestBody.put("MonthlyRate", 15000);
        requestBody.put("NumCompaniesWorked", 2);
        requestBody.put("OverTime", "No");
        requestBody.put("RelationshipSatisfaction", 3);
        requestBody.put("StockOptionLevel", 1);
        requestBody.put("TotalWorkingYears", 10);
        requestBody.put("TrainingTimesLastYear", 3);
        requestBody.put("WorkLifeBalance", 3);
        requestBody.put("YearsAtCompany", 5);
        requestBody.put("YearsInCurrentRole", 3);
        requestBody.put("YearsSinceLastPromotion", 1);
        requestBody.put("YearsWithCurrManager", 4);


        // Call the external API and log the response
        return webClient.post()
                .uri("http://localhost:8000/hr/predict")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(PredictionResponse.class)
                .doOnSuccess(response -> {
                    log.info("Successfully received prediction for employee '{}': {}", response.getEmployeeName(), response);
                })
                .doOnError(error -> {
                    log.error("Error calling prediction API for user ID {}: ", userId, error);
                });
    }
}