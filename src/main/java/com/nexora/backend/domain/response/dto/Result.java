package com.nexora.backend.domain.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private double score;
    private int start;
    private int end;
    private String answer;
}