package com.nexora.backend.emp_suggetions.controller;


import com.nexora.backend.domain.request.EmployeeSuggestionRequest;
import com.nexora.backend.domain.response.APIResponse;
import com.nexora.backend.emp_suggetions.service.EmployeeSuggestionService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@RestController
@RequestMapping("api/v1/suggestions")
@RequiredArgsConstructor
public class EmployeeSuggestionsController {

    @NonNull
    private final EmployeeSuggestionService employeeSuggestionService;

    @PostMapping("/save-employee-suggestion")
    public ResponseEntity<APIResponse> saveEmployeeSuggestion(@Valid @RequestBody EmployeeSuggestionRequest request) {
        log.info("Received request to save employee suggestion: {}", request);
        return employeeSuggestionService.saveEmployeeSuggestion(request);
    }

    @GetMapping("/get-all-suggestions")
    public ResponseEntity<APIResponse> getAllSuggestions(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get All Employee Suggestions - Page: {}, Size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return employeeSuggestionService.getAllSuggestions(pageable);
    }

    @GetMapping("/get-suggestion/{id}")
    public ResponseEntity<APIResponse> getSuggestionById(@PathVariable Long id) {
        log.info("Get Employee Suggestion By ID: {}", id);
        return employeeSuggestionService.getSuggestionById(id);
    }

    @GetMapping("/get-suggestions-by-employee-code/{employeeCode}")
    public ResponseEntity<APIResponse> getSuggestionsByEmployeeCode(@PathVariable String employeeCode) {
        log.info("Get Employee Suggestions By Employee Code: {}", employeeCode);
        return employeeSuggestionService.getSuggestionsByEmployeeCode(employeeCode);
    }

    @DeleteMapping("/delete-suggestion/{id}")
    public ResponseEntity<APIResponse> deleteSuggestion(@PathVariable Long id) {
        log.info("Delete Employee Suggestion By ID: {}", id);
        return employeeSuggestionService.deleteSuggestion(id);
    }
}
