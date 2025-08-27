package com.nexora.backend.model.service;

import com.nexora.backend.domain.response.dto.PredictionResponse;
import reactor.core.publisher.Mono;

public interface ModelService {
    Mono<PredictionResponse> getPredictionForEmployee(Integer userId);
}