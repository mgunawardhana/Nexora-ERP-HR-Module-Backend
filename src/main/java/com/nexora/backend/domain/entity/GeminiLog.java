package com.nexora.backend.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GeminiLog {
    private String sessionId;
    private String prompt;
    private String response;
    private LocalDateTime timestamp;
}