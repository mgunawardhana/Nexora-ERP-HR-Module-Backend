package com.nexora.backend.model.service.impl;

import com.nexora.backend.authentication.repository.EmployeeDetailsRepository;
import com.nexora.backend.domain.entity.EmployeeDetails;
import com.nexora.backend.domain.response.dto.PredictionResponse;
import com.nexora.backend.model.service.ModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    private final EmployeeDetailsRepository employeeDetailsRepository;
    private final WebClient webClient;

    @Override
    public Mono<PredictionResponse> getPredictionForEmployee(Integer userId) {
        Optional<EmployeeDetails> employeeDetailsOpt = employeeDetailsRepository.findByUserId(userId);

        if (employeeDetailsOpt.isEmpty()) {
            log.error("Employee not found with user ID: {}", userId);
            return Mono.error(new RuntimeException("Employee not found with user ID: " + userId));
        }

        EmployeeDetails employeeDetails = employeeDetailsOpt.get();
        log.info("Fetching prediction for employee: {}", employeeDetails.getEmployeeName());

        // Prepare the request body from EmployeeDetails
        var requestBody = new PredictionRequest(
                employeeDetails.getEmployeeName(),
                employeeDetails.getAge(),
                employeeDetails.getBusinessTravel(),
                employeeDetails.getDailyRate(),
                employeeDetails.getDepartment(),
                employeeDetails.getDistanceFromHome(),
                employeeDetails.getEducation(),
                employeeDetails.getEducationField(),
                employeeDetails.getEnvironmentSatisfaction(),
                employeeDetails.getGender(),
                employeeDetails.getHourlyRate(),
                employeeDetails.getJobInvolvement(),
                employeeDetails.getJobLevel(),
                employeeDetails.getJobRole(),
                employeeDetails.getJobSatisfaction(),
                employeeDetails.getMaritalStatus(),
                employeeDetails.getMonthlyIncome(),
                employeeDetails.getMonthlyRate(),
                employeeDetails.getNumCompaniesWorked(),
                employeeDetails.getOverTime(),
                employeeDetails.getRelationshipSatisfaction(),
                employeeDetails.getStockOptionLevel(),
                employeeDetails.getTotalWorkingYears(),
                employeeDetails.getTrainingTimesLastYear(),
                employeeDetails.getWorkLifeBalance(),
                employeeDetails.getYearsAtCompany(),
                employeeDetails.getYearsInCurrentRole(),
                employeeDetails.getYearsSinceLastPromotion(),
                employeeDetails.getYearsWithCurrManager()
        );

        // Make the API call using WebClient
        return webClient.post()
                .uri("http://localhost:8000/hr/predict")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(PredictionResponse.class)
                .doOnSuccess(response ->
                        log.info("Successfully received prediction for employee: {}", employeeDetails.getEmployeeName()))
                .doOnError(error ->
                        log.error("Error fetching prediction for employee {}: {}",
                                employeeDetails.getEmployeeName(), error.getMessage()));
    }

    // Inner class to represent the request body
    private record PredictionRequest(
            String employee_name,
            Integer Age,
            String BusinessTravel,
            Integer DailyRate,
            String Department,
            Integer DistanceFromHome,
            Integer Education,
            String EducationField,
            Integer EnvironmentSatisfaction,
            String Gender,
            Integer HourlyRate,
            Integer JobInvolvement,
            Integer JobLevel,
            String JobRole,
            Integer JobSatisfaction,
            String MaritalStatus,
            Integer MonthlyIncome,
            Integer MonthlyRate,
            Integer NumCompaniesWorked,
            String OverTime,
            Integer RelationshipSatisfaction,
            Integer StockOptionLevel,
            Integer TotalWorkingYears,
            Integer TrainingTimesLastYear,
            Integer WorkLifeBalance,
            Integer YearsAtCompany,
            Integer YearsInCurrentRole,
            Integer YearsSinceLastPromotion,
            Integer YearsWithCurrManager
    ) {}
}