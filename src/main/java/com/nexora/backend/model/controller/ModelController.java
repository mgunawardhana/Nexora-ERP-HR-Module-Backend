package com.nexora.backend.model.controller;

import com.nexora.backend.domain.entity.EmployeeDetails;
import com.nexora.backend.model.service.ModelService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@RestController
@RequestMapping("api/v1/model")
@RequiredArgsConstructor
public class ModelController {

    @NonNull
    private final ModelService modelService;

    @PostMapping("/find-by/{id}")
    public Optional<EmployeeDetails> markAttendance(@PathVariable Integer id) {
        log.info("Fetch Suggestions for user: {}", id);
        return modelService.generateSuggestionRelatedToEmployee(String.valueOf(id));
    }
}