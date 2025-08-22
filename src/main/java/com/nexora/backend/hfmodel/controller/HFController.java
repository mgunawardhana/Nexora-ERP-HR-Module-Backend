package com.nexora.backend.hfmodel.controller;

import com.nexora.backend.domain.request.QuestionAnsweringRequest;
import com.nexora.backend.domain.response.QuestionAnsweringResponse;
import com.nexora.backend.hfmodel.service.HFService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@RestController
@RequestMapping("api/v1/")
@RequiredArgsConstructor
public class HFController {

    private final HFService hfService;

    @PostMapping("/question-answering")
    public Mono<QuestionAnsweringResponse> getAnswer(@RequestBody QuestionAnsweringRequest request) {
        return hfService.getAnswer(request);
    }
}