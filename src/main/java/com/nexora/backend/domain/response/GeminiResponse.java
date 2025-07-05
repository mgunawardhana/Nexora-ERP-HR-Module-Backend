package com.nexora.backend.domain.response;


import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GeminiResponse {
    private boolean success;
    private String content;
    private String error;
}