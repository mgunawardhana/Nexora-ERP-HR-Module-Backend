package com.nexora.backend.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse {
    public String statusMessage;
    public String statusCode;
    public String transactionId;
    public String responseTime;
    public String origin;
    public String errorType;
    public Object result;
}