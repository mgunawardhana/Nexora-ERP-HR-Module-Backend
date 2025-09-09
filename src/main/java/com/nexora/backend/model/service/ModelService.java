package com.nexora.backend.model.service;

import com.nexora.backend.domain.request.GeminiApiRequest;
import com.nexora.backend.domain.response.APIResponse;
import org.springframework.http.ResponseEntity;

public interface ModelService {
    ResponseEntity<APIResponse> getPredictionForEmployee(Integer userId);

    ResponseEntity<APIResponse> getGeminiForAdvancedDecision(GeminiApiRequest request);
}