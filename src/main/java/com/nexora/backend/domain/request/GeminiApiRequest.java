// domain/request/GeminiApiRequest.java
package com.nexora.backend.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeminiApiRequest {
    private String employee;
    private String basicSuggestion;
    private String employeeId;
    private Double monthlySalary;
    private String gender;
    private Double performancePredictionValue;
}