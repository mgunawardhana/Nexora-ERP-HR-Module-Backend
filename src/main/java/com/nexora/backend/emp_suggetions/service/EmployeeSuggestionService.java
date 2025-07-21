package com.nexora.backend.emp_suggetions.service;

import com.nexora.backend.domain.request.EmployeeSuggestionRequest;
import com.nexora.backend.domain.response.APIResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface EmployeeSuggestionService {

    ResponseEntity<APIResponse> saveEmployeeSuggestion(EmployeeSuggestionRequest request);

    ResponseEntity<APIResponse> getAllSuggestions(Pageable pageable);

    ResponseEntity<APIResponse> getSuggestionById(Long id);

    ResponseEntity<APIResponse> getSuggestionsByEmployeeCode(String employeeCode);

    ResponseEntity<APIResponse> deleteSuggestion(Long id);
}