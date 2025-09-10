package com.nexora.backend.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeminiApiRequest {
    private List<Content> contents;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Content {
        private List<Part> parts;
        private String role; // It's good practice to define the role of the content creator.
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Part {
        private String text;
    }
}