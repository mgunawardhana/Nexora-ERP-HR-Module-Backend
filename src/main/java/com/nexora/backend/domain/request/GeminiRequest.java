package com.nexora.backend.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class GeminiRequest {
    @NotBlank(message = "Prompt cannot be empty")
    private String prompt;
    private Map<String, Object> options;
}
