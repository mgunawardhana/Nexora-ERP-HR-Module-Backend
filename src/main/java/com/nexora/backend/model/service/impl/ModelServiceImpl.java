package com.nexora.backend.model.service.impl;

import com.nexora.backend.authentication.repository.EmployeeDetailsRepository;
import com.nexora.backend.domain.entity.EmployeeDetails;

import com.nexora.backend.domain.request.PredictionRequest;
import com.nexora.backend.domain.response.dto.PredictionResponse;
import com.nexora.backend.model.service.ModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
            // If the employee is not found, return an error.
            return Mono.error(new RuntimeException("Employee not found with user ID: " + userId));
        }

        EmployeeDetails employeeDetails = employeeDetailsOpt.get();

        // --- THIS IS THE CORRECT, DYNAMIC APPROACH ---
        // Build the request body using the data from the database.
        // It will only use the fields available in your EmployeeDetails entity.
        PredictionRequest requestBody = PredictionRequest.from(employeeDetails);

        // Call the external API with the dynamically created request body.
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