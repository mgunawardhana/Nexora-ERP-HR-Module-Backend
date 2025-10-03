package com.nexora.backend.model.controller;

import com.nexora.backend.domain.request.GeminiApiRequest;
import com.nexora.backend.domain.response.APIResponse;
import com.nexora.backend.model.service.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/model")
@RequiredArgsConstructor
public class ModelController {

    private final ModelService modelService;

    @GetMapping("/predict/{id}")
    public ResponseEntity<APIResponse> predict(@PathVariable Integer id) {
        return modelService.getPredictionForEmployee(id);
    }

    @GetMapping("/gemini-for/advanced/decision")
    public ResponseEntity<APIResponse> getGeminiForAdvancedDecision(@RequestParam("prompt") String prompt) {
        return modelService.getGeminiForAdvancedDecision(prompt);
    }

    @GetMapping("/top-5-kpi")
    public ResponseEntity<APIResponse> getTop5KpiEmployees() {
        return modelService.getTop5KpiEmployees();
    }
}