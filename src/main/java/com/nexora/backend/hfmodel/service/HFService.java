package com.nexora.backend.hfmodel.service;

import com.nexora.backend.domain.request.QuestionAnsweringRequest;
import com.nexora.backend.domain.response.QuestionAnsweringResponse;
import reactor.core.publisher.Mono;

public interface HFService {
    Mono<QuestionAnsweringResponse> getAnswer(QuestionAnsweringRequest request);
}