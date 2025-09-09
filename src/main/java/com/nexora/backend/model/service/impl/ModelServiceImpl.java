package com.nexora.backend.model.service.impl;

import com.nexora.backend.authentication.repository.EmployeeDetailsRepository;
import com.nexora.backend.domain.entity.EmployeeDetails;
import com.nexora.backend.domain.request.GeminiApiRequest;
import com.nexora.backend.domain.response.APIResponse;
import com.nexora.backend.domain.response.dto.PredictionResponse;
import com.nexora.backend.model.service.ModelService;
import com.nexora.backend.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    private final EmployeeDetailsRepository employeeDetailsRepository;
    private final WebClient webClient;
    private final ResponseUtil responseUtil;
    private final RestTemplate restTemplate;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public ResponseEntity<APIResponse> getGeminiForAdvancedDecision(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("X-goog-api-key", geminiApiKey);

            GeminiApiRequest.Part part = GeminiApiRequest.Part.builder()
                    .text(prompt)
                    .build();

            GeminiApiRequest.Content content = GeminiApiRequest.Content.builder()
                    .parts(Collections.singletonList(part))
                    .build();

            GeminiApiRequest request = GeminiApiRequest.builder()
                    .contents(Collections.singletonList(content))
                    .build();

            HttpEntity<GeminiApiRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(geminiApiUrl, entity, String.class);

            return responseUtil.wrapSuccess(response.getBody(), HttpStatus.OK);

        } catch (Exception e) {
            return responseUtil.wrapError("Error communicating with Gemini API", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<APIResponse> getPredictionForEmployee(Integer userId) {
        try {
            Optional<EmployeeDetails> employeeDetailsOpt = employeeDetailsRepository.findByUserId(userId);

            if (employeeDetailsOpt.isEmpty()) {
                log.error("Employee not found with user ID: {}", userId);
                return responseUtil.wrapError(
                        "Employee not found with user ID: " + userId,
                        "EMPLOYEE_NOT_FOUND",
                        HttpStatus.NOT_FOUND
                );
            }

            EmployeeDetails employeeDetails = employeeDetailsOpt.get();
            log.info("Fetching prediction for employee: {}", employeeDetails.getEmployeeName());

            // ---------- BUILD REQUEST BODY ----------
            var requestBody = new PredictionRequest(
                    employeeDetails.getEmployeeName(),
                    employeeDetails.getAge(),
                    employeeDetails.getBusinessTravel(),
                    employeeDetails.getDailyRate() != null ? employeeDetails.getDailyRate().doubleValue() : null,
                    employeeDetails.getDepartment(),
                    employeeDetails.getDistanceFromHome(),
                    employeeDetails.getEducation(),          // numeric 1–5
                    employeeDetails.getEducationField(),     // string like "Life Sciences"
                    employeeDetails.getEnvironmentSatisfaction(),
                    employeeDetails.getGender(),
                    employeeDetails.getHourlyRate() != null ? employeeDetails.getHourlyRate().doubleValue() : null,
                    employeeDetails.getJobInvolvement(),
                    employeeDetails.getJobLevel(),
                    employeeDetails.getJobRole(),
                    employeeDetails.getJobSatisfaction(),
                    employeeDetails.getMaritalStatus(),
                    employeeDetails.getMonthlyIncome() != null ? employeeDetails.getMonthlyIncome().doubleValue() : null,
                    employeeDetails.getMonthlyRate() != null ? employeeDetails.getMonthlyRate().doubleValue() : null,
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

            // ---------- CALL PYTHON MODEL ----------
            PredictionResponse predictionResponse = webClient.post()
                    .uri("http://localhost:8000/hr/predict")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(PredictionResponse.class)
                    .doOnNext(raw -> log.info("✅ Raw prediction response: {}", raw))
                    .doOnSuccess(response ->
                            log.info("✅ Successfully received prediction for employee: {}", employeeDetails.getEmployeeName()))
                    .doOnError(error ->
                            log.error("❌ Error fetching prediction for employee {}: {}",
                                    employeeDetails.getEmployeeName(), error.getMessage()))
                    .block(); // Convert Mono to synchronous call

            if (predictionResponse == null) {
                log.error("Received null response from prediction service for employee: {}", employeeDetails.getEmployeeName());
                return responseUtil.wrapError(
                        "Failed to get prediction from external service",
                        "PREDICTION_SERVICE_ERROR",
                        HttpStatus.SERVICE_UNAVAILABLE
                );
            }

            log.info("✅ Prediction successful for employee: {}", employeeDetails.getEmployeeName());
            return responseUtil.wrapSuccess(predictionResponse, HttpStatus.OK);

        } catch (Exception e) {
            log.error("❌ Unexpected error while getting prediction for user ID {}: {}", userId, e.getMessage(), e);
            return responseUtil.wrapError(
                    "An unexpected error occurred while processing the prediction request",
                    "INTERNAL_SERVER_ERROR",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public ResponseEntity<APIResponse> getGeminiForAdvancedDecision(GeminiApiRequest request) {
        return null;
    }

    /**
     * Inner record to represent the request body sent to the Python prediction API.
     */
    private record PredictionRequest(
            String employee_name,
            Integer Age,
            String BusinessTravel,
            Double DailyRate,
            String Department,
            Integer DistanceFromHome,
            Integer Education,
            String EducationField,
            Integer EnvironmentSatisfaction,
            String Gender,
            Double HourlyRate,
            Integer JobInvolvement,
            Integer JobLevel,
            String JobRole,
            Integer JobSatisfaction,
            String MaritalStatus,
            Double MonthlyIncome,
            Double MonthlyRate,
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