package com.nexora.backend.domain.response;

import com.nexora.backend.domain.response.dto.Result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnsweringResponse {
    private String status;
    private Result result;
}