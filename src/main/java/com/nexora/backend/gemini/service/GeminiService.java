package com.nexora.backend.gemini.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private boolean isHrRelated(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            return false;
        }
        List<String> hrKeywords = Arrays.asList("employee", "performance", "kpi", "hr", "evaluation", "appreciation", "warning", "summary");
        String lowerCasePrompt = prompt.toLowerCase();
        return hrKeywords.stream().anyMatch(lowerCasePrompt::contains);
    }

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

    public Mono<String> generateContent(String prompt, Map<String, Object> options) {
        if (!isHrRelated(prompt)) {
            return Mono.just("I am an HR AI model and can only assist with HR-related inquiries.");
        }

        try {
            // Extract employee name from prompt using regex
            Pattern namePattern = Pattern.compile("(?i)name:\\s*([^\\n\\r-]+?)(?=\\s*-|\\n|\\r|$)");
            Matcher matcher = namePattern.matcher(prompt);

            if (matcher.find()) {
                String fullName = matcher.group(1).trim();
                String[] nameParts = fullName.trim().split("\\s+");

                if (nameParts.length >= 2) {
                    String firstName = nameParts[0];
                    String lastName = String.join(" ", Arrays.copyOfRange(nameParts, 1, nameParts.length));
                    String employeeApiUrl = String.format("http://localhost:8000/employee/%s/%s", firstName, lastName);

                    log.debug("Fetching employee data from: {}", employeeApiUrl);

                    // Fetch additional employee data and enhance prompt
                    return webClient.get()
                            .uri(employeeApiUrl)
                            .retrieve()
                            .bodyToMono(String.class)
                            .timeout(Duration.ofMillis(5000))
                            .map(response -> {
                                try {
                                    // Parse the JSON response
                                    ObjectMapper mapper = new ObjectMapper();
                                    JsonNode jsonNode = mapper.readTree(response);
                                    JsonNode employee = jsonNode.get("employee");

                                    if (employee != null) {
                                        // Extract employee metrics
                                        double monthlyKpi = employee.has("monthly_kpi") ? employee.get("monthly_kpi").asDouble() : 0.0;
                                        double actualHours = employee.has("total_actual_hours") ? employee.get("total_actual_hours").asDouble() : 0.0;
                                        double scheduledHours = employee.has("total_scheduled_hours") ? employee.get("total_scheduled_hours").asDouble() : 0.0;
                                        int workdayCount = employee.has("workday_count") ? employee.get("workday_count").asInt() : 0;
                                        String kpiPercentage = employee.has("kpi_percentage") ? employee.get("kpi_percentage").asText() : "N/A";

                                        // Calculate attendance percentage
                                        double attendanceRate = scheduledHours > 0 ? (actualHours / scheduledHours) * 100 : 0.0;

                                        // Enhance the original prompt with additional data and decision criteria
                                        StringBuilder enhancedPrompt = new StringBuilder(prompt);
                                        enhancedPrompt.append("\n\nAdditional Employee Metrics:")
                                                .append("\n- Monthly KPI: ").append(monthlyKpi)
                                                .append("\n- KPI Percentage: ").append(kpiPercentage)
                                                .append("\n- Total Actual Hours: ").append(actualHours)
                                                .append("\n- Total Scheduled Hours: ").append(scheduledHours)
                                                .append("\n- Workday Count: ").append(workdayCount)
                                                .append("\n- Attendance Rate: ").append(String.format("%.1f%%", attendanceRate))
                                                .append("\n\nHR Decision Analysis:")
                                                .append("\nBased on the comprehensive employee metrics, provide a clear HR recommendation from the following options:")
                                                .append("\n1. PROMOTE - Employee exceeds expectations and is ready for advancement")
                                                .append("\n2. SALARY INCREMENT - Employee meets expectations and deserves compensation increase")
                                                .append("\n3. RETAIN - Employee performs adequately, maintain current status with development plan")
                                                .append("\n4. PERFORMANCE IMPROVEMENT PLAN (PIP) - Employee underperforms, needs structured improvement")
                                                .append("\n5. LAY OFF - Employee consistently fails to meet minimum standards")
                                                .append("\n\nDecision Criteria:")
                                                .append("\n- KPI Score >= 0.8 AND Attendance >= 90% = Consider PROMOTE")
                                                .append("\n- KPI Score >= 0.6 AND Attendance >= 85% = Consider SALARY INCREMENT")
                                                .append("\n- KPI Score >= 0.4 AND Attendance >= 75% = RETAIN with development")
                                                .append("\n- KPI Score >= 0.2 AND Attendance >= 60% = PERFORMANCE IMPROVEMENT PLAN")
                                                .append("\n- KPI Score < 0.2 OR Attendance < 60% = Consider LAY OFF")
                                                .append("\n\nProvide the evaluation paragraph followed by:")
                                                .append("\n- **HR DECISION**: [DECISION_TYPE]")
                                                .append("\n- **JUSTIFICATION**: [Specific reasons based on metrics]")
                                                .append("\n- **ACTION ITEMS**: [Concrete next steps]");

                                        return enhancedPrompt.toString();
                                    }
                                } catch (Exception e) {
                                    log.warn("Failed to parse employee data response: {}", e.getMessage());
                                }
                                return prompt; // Return original prompt if parsing fails
                            })
                            .onErrorResume(error -> {
                                log.warn("Failed to fetch employee data for {} {}, proceeding with original prompt: {}",
                                        firstName, lastName, error.getMessage());
                                return Mono.just(prompt);
                            })
                            .flatMap(enhancedPrompt -> {
                                // Generate content with enhanced prompt
                                Map<String, Object> requestBody = createRequestBody(enhancedPrompt, options);
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
                                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                                                .maxBackoff(Duration.ofSeconds(10))
                                                .filter(this::isRetryableError)
                                                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                                                    Throwable failure = retrySignal.failure();
                                                    log.error("Max retries exceeded for Gemini API after {} attempts",
                                                            retrySignal.totalRetries(), failure);
                                                    return new RuntimeException("Gemini API is temporarily unavailable after " +
                                                            retrySignal.totalRetries() + " attempts. Please try again later.", failure);
                                                })
                                        )
                                        .map(this::extractContentFromResponse)
                                        .doOnError(error -> log.error("Error calling Gemini API", error))
                                        .onErrorResume(this::handleError);
                            });
                }
            }

            // If no name found or name parsing failed, proceed with original prompt
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
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                            .maxBackoff(Duration.ofSeconds(10))
                            .filter(this::isRetryableError)
                            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                                Throwable failure = retrySignal.failure();
                                log.error("Max retries exceeded for Gemini API after {} attempts",
                                        retrySignal.totalRetries(), failure);
                                return new RuntimeException("Gemini API is temporarily unavailable after " +
                                        retrySignal.totalRetries() + " attempts. Please try again later.", failure);
                            })
                    )
                    .map(this::extractContentFromResponse)
                    .doOnError(error -> log.error("Error calling Gemini API", error))
                    .onErrorResume(this::handleError);

        } catch (Exception e) {
            log.error("Error preparing Gemini API request", e);
            return Mono.just("Error preparing request: " + e.getMessage());
        }
    }

    /**
     * Determines if an error is retryable
     */
    private boolean isRetryableError(Throwable throwable) {
        if (throwable instanceof WebClientResponseException) {
            WebClientResponseException ex = (WebClientResponseException) throwable;
            int statusCode = ex.getStatusCode().value();

            // Retry on server errors and rate limiting
            boolean shouldRetry = statusCode == 503 || // Service Unavailable
                    statusCode == 502 || // Bad Gateway
                    statusCode == 504 || // Gateway Timeout
                    statusCode == 429 || // Too Many Requests
                    statusCode == 500;   // Internal Server Error

            if (shouldRetry) {
                log.warn("Retryable error encountered: {} - {}", statusCode, ex.getMessage());
            } else {
                log.error("Non-retryable error: {} - {}", statusCode, ex.getMessage());
            }

            return shouldRetry;
        }

        // Retry on connection issues
        if (throwable instanceof java.net.ConnectException) {
            log.warn("Connection error encountered, will retry: {}", throwable.getMessage());
            return true;
        }

        // Retry on timeout exceptions
        if (throwable instanceof java.util.concurrent.TimeoutException) {
            log.warn("Timeout error encountered, will retry: {}", throwable.getMessage());
            return true;
        }

        return false;
    }

    /**
     * Handles errors and provides user-friendly messages
     */
    private Mono<String> handleError(Throwable error) {
        if (error instanceof WebClientResponseException) {
            WebClientResponseException ex = (WebClientResponseException) error;
            int statusCode = ex.getStatusCode().value();

            switch (statusCode) {
                case 503:
                    return Mono.just("The AI service is temporarily overloaded. Please try again in a few moments.");
                case 502:
                case 504:
                    return Mono.just("The AI service is experiencing connectivity issues. Please try again shortly.");
                case 429:
                    return Mono.just("Too many requests. Please wait a moment before trying again.");
                case 401:
                    return Mono.just("Authentication failed. Please check the API configuration.");
                case 403:
                    return Mono.just("Access denied. Please check your API permissions.");
                case 400:
                    return Mono.just("Invalid request. Please check your input and try again.");
                default:
                    return Mono.just("AI service error (" + statusCode + "): " + ex.getMessage());
            }
        }

        if (error instanceof java.net.ConnectException) {
            return Mono.just("Unable to connect to the AI service. Please check your internet connection and try again.");
        }

        if (error instanceof java.util.concurrent.TimeoutException) {
            return Mono.just("The request timed out. Please try again with a shorter prompt or check your connection.");
        }

        if (error instanceof RuntimeException && error.getMessage().contains("temporarily unavailable")) {
            return Mono.just(error.getMessage());
        }

        return Mono.just("An unexpected error occurred: " + error.getMessage());
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