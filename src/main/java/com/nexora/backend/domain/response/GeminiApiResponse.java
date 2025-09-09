// domain/response/GeminiApiResponse.java
package com.nexora.backend.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeminiApiResponse {
    // Define fields to capture the response from the Gemini API
    // For example:
    private String generatedContent;
}