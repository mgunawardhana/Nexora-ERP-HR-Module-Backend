package com.nexora.backend.hfmodel.service;

import com.nexora.backend.domain.request.QuestionAnsweringRequest;
import com.nexora.backend.domain.response.QuestionAnsweringResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class HFServiceImpl implements HFService {

    private final WebClient webClient;

    @Override
    public Mono<QuestionAnsweringResponse> getAnswer(QuestionAnsweringRequest request) {
        return webClient.post()
                .uri("http://localhost:8000/question-answering")
                .body(Mono.just(request), QuestionAnsweringRequest.class)
                .retrieve()
                .bodyToMono(QuestionAnsweringResponse.class);
    }
}