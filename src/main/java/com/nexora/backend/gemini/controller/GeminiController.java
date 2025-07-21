package com.nexora.backend.gemini.controller;

import com.nexora.backend.domain.request.GeminiRequest;
import com.nexora.backend.domain.response.GeminiResponse;
import com.nexora.backend.gemini.service.GeminiService;
import com.nexora.backend.gemini.repository.GeminiRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.UUID;

@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
@Slf4j
public class GeminiController {

    private final GeminiService geminiService;
    private final GeminiRepository geminiRepository;

    @PostMapping("/generate")
    public Mono<ResponseEntity<GeminiResponse>> generateContent(@Valid @RequestBody GeminiRequest request) {
        String sessionId = UUID.randomUUID().toString();
        log.info("Received request to generate content with prompt: {}, sessionId: {}", request.getPrompt(), sessionId);

        return geminiService.generateContent(request.getPrompt(), request.getOptions())
                .map(content -> {
                    if (content.startsWith("Error:") || content.startsWith("The AI service") ||
                            content.startsWith("Unable to connect") || content.startsWith("Too many requests") ||
                            content.startsWith("Authentication failed") || content.startsWith("Access denied")) {

                        log.warn("API returned error response: {}", content);
                        GeminiResponse response = new GeminiResponse(false, content, null);
                        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
                    } else {
                        geminiRepository.saveLog(sessionId, request.getPrompt(), content);
                        GeminiResponse response = new GeminiResponse(true, content, null);
                        return ResponseEntity.ok(response);
                    }
                })
                .onErrorResume(error -> {
                    log.error("Unexpected error generating content for session {}", sessionId, error);
                    GeminiResponse response = new GeminiResponse(false, null,
                            "An unexpected error occurred. Please try again later.");
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
                });
    }

    @GetMapping("/generate-simple")
    public Mono<ResponseEntity<GeminiResponse>> generateContentSimple(
            @RequestParam @NotBlank(message = "Prompt cannot be empty") String prompt) {
        String sessionId = UUID.randomUUID().toString();
        log.info("Received GET request with prompt: {}, sessionId: {}", prompt, sessionId);

        return geminiService.generateContent(prompt)
                .map(content -> {
                    if (content.startsWith("Error:") || content.startsWith("The AI service") ||
                            content.startsWith("Unable to connect") || content.startsWith("Too many requests") ||
                            content.startsWith("Authentication failed") || content.startsWith("Access denied")) {

                        log.warn("API returned error response: {}", content);
                        GeminiResponse response = new GeminiResponse(false, content, null);
                        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
                    } else {
                        geminiRepository.saveLog(sessionId, prompt, content);
                        GeminiResponse response = new GeminiResponse(true, content, null);
                        return ResponseEntity.ok(response);
                    }
                })
                .onErrorResume(error -> {
                    log.error("Unexpected error generating content for session {}", sessionId, error);
                    GeminiResponse response = new GeminiResponse(false, null,
                            "An unexpected error occurred. Please try again later.");
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
                });
    }

    @PostMapping("/hr-evaluation")
    public Mono<ResponseEntity<GeminiResponse>> generateHrEvaluation(
            @Valid @RequestBody java.util.Map<String, Object> employeeData) {
        String sessionId = UUID.randomUUID().toString();
        log.info("Received HR evaluation request for employee: {}, sessionId: {}",
                employeeData.get("employeeName"), sessionId);

        return geminiService.generateHrEvaluation(employeeData)
                .map(content -> {
                    if (content.startsWith("Error:") || content.startsWith("The AI service") ||
                            content.startsWith("Unable to connect") || content.startsWith("Too many requests") ||
                            content.startsWith("Authentication failed") || content.startsWith("Access denied")) {

                        log.warn("HR evaluation API returned error response: {}", content);
                        GeminiResponse response = new GeminiResponse(false, content, null);
                        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
                    } else {
                        geminiRepository.saveLog(sessionId, "HR Evaluation for " + employeeData.get("employeeName"), content);
                        GeminiResponse response = new GeminiResponse(true, content, null);
                        return ResponseEntity.ok(response);
                    }
                })
                .onErrorResume(error -> {
                    log.error("Unexpected error generating HR evaluation for session {}", sessionId, error);
                    GeminiResponse response = new GeminiResponse(false, null,
                            "An unexpected error occurred while generating HR evaluation. Please try again later.");
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
                });
    }
}