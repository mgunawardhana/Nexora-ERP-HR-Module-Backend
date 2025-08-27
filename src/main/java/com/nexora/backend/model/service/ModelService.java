package com.nexora.backend.model.service;

import com.nexora.backend.domain.entity.EmployeeDetails;

import java.util.Optional;

public interface ModelService {

    Optional<EmployeeDetails> generateSuggestionRelatedToEmployee(String id);
}
