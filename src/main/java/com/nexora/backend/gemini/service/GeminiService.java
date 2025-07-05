package com.nexora.backend.gemini.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.base-url}")
    private String baseUrl;

    @Value("${gemini.api.model}")
    private String model;

    @Value("${gemini.api.timeout}")
    private int timeout;

    /**
     * Generates an HR-style performance evaluation based on employee data.
     * It dynamically creates a prompt for either appreciation or a constructive warning
     * based on the employee's Monthly KPI score.
     *
     * @param employeeData A map containing the employee's performance data. Expected keys:
     * "employeeName", "monthlyKpi", "kpiPercentage",
     * "totalScheduledHours", "workdayCount".
     * @return A Mono<String> containing the AI-generated HR evaluation.
     */
    public Mono<String> generateHrEvaluation(Map<String, Object> employeeData) {
        try {
            // Build the specific, dynamic prompt for the HR evaluation
            String dynamicPrompt = buildHrEvaluationPrompt(employeeData);

            // Use the existing generic method to call the API with the new prompt
            return generateContent(dynamicPrompt, null);

        } catch (IllegalArgumentException e) {
            log.error("Invalid data provided for HR evaluation.", e);
            return Mono.just("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred while preparing the HR evaluation prompt.", e);
            return Mono.just("Error: " + e.getMessage());
        }
    }

    /**
     * Helper method to construct the HR evaluation prompt.
     *
     * @param data The map with employee data.
     * @return A formatted prompt string.
     */
    private String buildHrEvaluationPrompt(Map<String, Object> data) {
        Object kpiObject = data.get("monthlyKpi");
        if (Objects.isNull(kpiObject)) {
            throw new IllegalArgumentException("Required key 'monthlyKpi' is missing from employee data.");
        }

        double kpi;
        try {
            kpi = ((Number) kpiObject).doubleValue();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("'monthlyKpi' must be a numeric value.", e);
        }

        // Define the KPI threshold for appreciation vs. warning.
        final double APPRECIATION_THRESHOLD = 0.90;

        String basePrompt;
        String performanceQualifier;

        if (kpi >= APPRECIATION_THRESHOLD) {
            basePrompt = "Based on the following employee performance data, provide a professional, HR-style appreciation summary. The summary should be suitable for HR communication and include at least 3 relevant points highlighting the employee's positive contributions.";
            performanceQualifier = "This employee has exceeded expectations.";
        } else {
            basePrompt = "Based on the following employee performance data, provide a professional, HR-style constructive warning summary. The summary should be suitable for HR communication and include at least 3 relevant, constructive points for improvement.";
            performanceQualifier = "This employee's performance requires attention.";
        }

        return String.format(
                "%s\n\n" +
                        "Employee Name: %s\n" +
                        "Monthly KPI Score: %s (%s)\n" +
                        "Total Scheduled Hours: %s\n" +
                        "Workdays Count: %s\n\n" +
                        "The ideal KPI score is 1.0 (100%%). %s",
                basePrompt,
                data.get("employeeName"),
                data.get("monthlyKpi"),
                data.get("kpiPercentage"),
                data.get("totalScheduledHours"),
                data.get("workdayCount"),
                performanceQualifier
        );
    }

    /**
     * Generic method to generate content with a simple prompt.
     *
     * @param prompt The text prompt to send to the API.
     * @return A Mono<String> with the response.
     */
    public Mono<String> generateContent(String prompt) {
        return generateContent(prompt, null);
    }

    /**
     * Core method to generate content with advanced options.
     *
     * @param prompt  The text prompt.
     * @param options A map with advanced generation settings (temperature, maxTokens, etc.).
     * @return A Mono<String> with the response.
     */
    public Mono<String> generateContent(String prompt, Map<String, Object> options) {
        try {
            Map<String, Object> requestBody = createRequestBody(prompt, options);
            String url = String.format("%s/models/%s:generateContent?key=%s", baseUrl, model, apiKey);

            log.debug("Sending request to Gemini API: {}", url);
            log.debug("Request body: {}", requestBody);

            return webClient.post()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(timeout))
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                            .filter(throwable -> throwable instanceof java.net.ConnectException))
                    .map(this::extractContentFromResponse)
                    .doOnError(error -> log.error("Error calling Gemini API", error))
                    .onErrorResume(error -> Mono.just("Error: " + error.getMessage()));

        } catch (Exception e) {
            log.error("Error preparing Gemini API request", e);
            return Mono.just("Error preparing request: " + e.getMessage());
        }
    }

    private Map<String, Object> createRequestBody(String prompt, Map<String, Object> options) {
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));
        requestBody.put("contents", List.of(content));

        if (options != null && !options.isEmpty()) {
            Map<String, Object> generationConfig = new HashMap<>();
            if (options.containsKey("temperature")) {
                generationConfig.put("temperature", Double.parseDouble(options.get("temperature").toString()));
            }
            if (options.containsKey("maxOutputTokens")) {
                generationConfig.put("maxOutputTokens", Integer.parseInt(options.get("maxOutputTokens").toString()));
            }
            // Add other configs like topP, topK as needed
            if (!generationConfig.isEmpty()) {
                requestBody.put("generationConfig", generationConfig);
            }
        }
        return requestBody;
    }

    private String extractContentFromResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode candidates = jsonNode.path("candidates");
            if (candidates.isArray() && !candidates.isEmpty()) {
                JsonNode parts = candidates.get(0).path("content").path("parts");
                if (parts.isArray() && !parts.isEmpty()) {
                    return parts.get(0).path("text").asText();
                }
            }
            // Handle cases where the response is blocked or has no content
            JsonNode promptFeedback = jsonNode.path("promptFeedback");
            if (!promptFeedback.isMissingNode()) {
                log.warn("Prompt was blocked. Feedback: {}", promptFeedback.toString());
                return "Error: The request was blocked due to safety settings. " + promptFeedback.path("blockReason").asText();
            }
            log.warn("Unexpected response format: {}", response);
            return "No content found in response";
        } catch (Exception e) {
            log.error("Error parsing Gemini response", e);
            return "Error parsing response: " + e.getMessage();
        }
    }
}