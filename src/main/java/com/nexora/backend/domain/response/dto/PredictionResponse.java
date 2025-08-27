package com.nexora.backend.domain.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PredictionResponse {
    private String status;
    private Prediction prediction;
    @JsonProperty("employee_name")
    private String employeeName;

    @Data
    public static class Prediction {
        @JsonProperty("performance_rating")
        private int performanceRating;
        private double confidence;
        private String suggestions;
    }
}