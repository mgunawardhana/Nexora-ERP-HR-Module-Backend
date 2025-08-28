package com.nexora.backend.domain.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PredictionResponse {
    private String status;

    @JsonProperty("employee_name")
    private String employeeName;

    private Prediction prediction;

    @Data
    public static class Prediction {
        @JsonProperty("performance_rating")
        private Integer performanceRating;

        private Double confidence;
        private String suggestions;
    }
}
