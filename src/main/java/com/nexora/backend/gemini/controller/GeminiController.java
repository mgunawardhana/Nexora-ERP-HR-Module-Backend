package com.nexora.backend.gemini.controller;

import com.nexora.backend.domain.request.GeminiRequest;
import com.nexora.backend.domain.response.GeminiResponse;
import com.nexora.backend.gemini.service.GeminiService;
import com.nexora.backend.gemini.repository.GeminiRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        Mono<ResponseEntity<GeminiResponse>> errorGeneratingContent = geminiService.generateContent(request.getPrompt(), request.getOptions())
                .map(content -> {
                    geminiRepository.saveLog(sessionId, request.getPrompt(), content);
                    GeminiResponse response = new GeminiResponse(true, content, null);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(error -> {
                    log.error("Error generating content", error);
                    GeminiResponse response = new GeminiResponse(false, null, error.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().body(response));
                });

        log.warn(errorGeneratingContent.toString());
        return errorGeneratingContent;
    }

    @GetMapping("/generate-simple")
    public Mono<ResponseEntity<GeminiResponse>> generateContentSimple(
            @RequestParam @NotBlank(message = "Prompt cannot be empty") String prompt) {
        String sessionId = UUID.randomUUID().toString();
        log.info("Received GET request with prompt: {}, sessionId: {}", prompt, sessionId);

        return geminiService.generateContent(prompt)
                .map(content -> {
                    geminiRepository.saveLog(sessionId, prompt, content);
                    GeminiResponse response = new GeminiResponse(true, content, null);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(error -> {
                    log.error("Error generating content", error);
                    GeminiResponse response = new GeminiResponse(false, null, error.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().body(response));
                });
    }
}