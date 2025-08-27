package com.nexora.backend.model.service.impl;

import com.nexora.backend.authentication.repository.EmployeeDetailsRepository;
import com.nexora.backend.domain.entity.EmployeeDetails;

import com.nexora.backend.domain.entity.User;
import com.nexora.backend.domain.response.dto.PredictionResponse;
import com.nexora.backend.model.service.ModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Period;
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


        // Build request body dynamically from EmployeeDetails
        Map<String, Object> requestBody = new HashMap<>();

        // Call ML API using WebClient
        return webClient.post()
                .uri("http://localhost:8000/hr/predict")
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(PredictionResponse.class)
                .doOnNext(resp -> log.info("Prediction Response: {}", resp));

    }

    private int calculateAge(LocalDate dob) {
        if (dob == null) return 0;
        return Period.between(dob, LocalDate.now()).getYears();
    }

    /**
     * Helper to calculate years at company
     */
    private int calculateYearsAtCompany(LocalDate joinDate) {
        if (joinDate == null) return 0;
        return Period.between(joinDate, LocalDate.now()).getYears();
    }

}