package com.nexora.backend.emp_suggetions.service;

import com.nexora.backend.domain.entity.EmployeeSuggestion;
import com.nexora.backend.domain.request.EmployeeSuggestionRequest;
import com.nexora.backend.domain.response.APIResponse;
import com.nexora.backend.emp_suggetions.repository.EmployeeSuggestionRepository;
import com.nexora.backend.util.ResponseUtil;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeSuggestionServiceImpl implements EmployeeSuggestionService {

    @NonNull
    private final EmployeeSuggestionRepository employeeSuggestionRepository;

    @NonNull
    private final ResponseUtil responseUtil;

    // Fixed Service Method
    @Override
    public ResponseEntity<APIResponse> saveEmployeeSuggestion(EmployeeSuggestionRequest request) {
        try {
            log.info("Saving employee suggestion for: {} {}", request.getFirstName(), request.getLastName());

            // Generate fullName if not provided
            String fullName = generateFullName(request);

            EmployeeSuggestion suggestion = EmployeeSuggestion.builder()
                    .firstName(processStringField(request.getFirstName(), "N/A"))
                    .lastName(processStringField(request.getLastName(), "N/A"))
                    .fullName(fullName)
                    .department(processStringField(request.getDepartment(), "N/A"))
                    .employeeCode(processStringField(request.getEmployeeCode(), "N/A"))
                    .suggestion(request.getSuggestion() != null ? request.getSuggestion().trim() : null)
                    .savedAt(request.getTimestamp() != null ? request.getTimestamp() : LocalDateTime.now())
                    .build();

            EmployeeSuggestion saved = employeeSuggestionRepository.save(suggestion);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("id", saved.getId());
            responseData.put("firstName", saved.getFirstName());
            responseData.put("lastName", saved.getLastName());
            responseData.put("fullName", saved.getFullName());
            responseData.put("department", saved.getDepartment());
            responseData.put("employeeCode", saved.getEmployeeCode());
            responseData.put("suggestion", saved.getSuggestion());
            responseData.put("savedAt", saved.getSavedAt());
            responseData.put("message", "Employee suggestion saved successfully");

            return responseUtil.wrapSuccess(responseData, HttpStatus.CREATED);

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation when saving employee suggestion for {} {}: {}",
                    request.getFirstName(), request.getLastName(), e.getMessage(), e);
            return responseUtil.wrapError("Invalid data provided for employee suggestion",
                    "Please check the provided data and try again", HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            log.error("Error saving employee suggestion for {} {}: {}",
                    request.getFirstName(), request.getLastName(), e.getMessage(), e);
            return responseUtil.wrapError("Failed to save employee suggestion",
                    e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper method to handle null/empty strings
    private String processStringField(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    // Helper method to generate full name
    private String generateFullName(EmployeeSuggestionRequest request) {
        String fullName = request.getFullName();
        if (fullName != null && !fullName.trim().isEmpty()) {
            return fullName.trim();
        }

        String first = processStringField(request.getFirstName(), "");
        String last = processStringField(request.getLastName(), "");

        if (!first.isEmpty() && !last.isEmpty()) {
            return first + " " + last;
        } else if (!first.isEmpty()) {
            return first;
        } else if (!last.isEmpty()) {
            return last;
        } else {
            return "N/A";
        }
    }


    @Override
    public ResponseEntity<APIResponse> getAllSuggestions(Pageable pageable) {
        try {
            log.info("Retrieving all employee suggestions with pagination: {}", pageable);

            Page<EmployeeSuggestion> suggestionsPage = employeeSuggestionRepository.findAll(pageable);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", suggestionsPage.getContent());
            responseData.put("totalElements", suggestionsPage.getTotalElements());
            responseData.put("totalPages", suggestionsPage.getTotalPages());
            responseData.put("currentPage", suggestionsPage.getNumber());
            responseData.put("pageSize", suggestionsPage.getSize());
            responseData.put("first", suggestionsPage.isFirst());
            responseData.put("last", suggestionsPage.isLast());

            return responseUtil.wrapSuccess(responseData, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error retrieving employee suggestions: {}", e.getMessage(), e);
            return responseUtil.wrapError("Failed to retrieve employee suggestions",
                    e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<APIResponse> getSuggestionById(Long id) {
        try {
            log.info("Retrieving employee suggestion by ID: {}", id);

            Optional<EmployeeSuggestion> suggestionOpt = employeeSuggestionRepository.findById(id);

            if (suggestionOpt.isPresent()) {
                return responseUtil.wrapSuccess(suggestionOpt.get(), HttpStatus.OK);
            } else {
                return responseUtil.wrapError("Employee suggestion not found",
                        "No suggestion found with ID: " + id, HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            log.error("Error retrieving employee suggestion by ID {}: {}", id, e.getMessage(), e);
            return responseUtil.wrapError("Failed to retrieve employee suggestion",
                    e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<APIResponse> getSuggestionsByEmployeeCode(String employeeCode) {
        try {
            log.info("Retrieving employee suggestions by employee code: {}", employeeCode);

            List<EmployeeSuggestion> suggestions = employeeSuggestionRepository.findByEmployeeCode(employeeCode);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("employeeCode", employeeCode);
            responseData.put("suggestions", suggestions);
            responseData.put("count", suggestions.size());

            return responseUtil.wrapSuccess(responseData, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error retrieving suggestions for employee code {}: {}", employeeCode, e.getMessage(), e);
            return responseUtil.wrapError("Failed to retrieve suggestions by employee code",
                    e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<APIResponse> deleteSuggestion(Long id) {
        try {
            log.info("Deleting employee suggestion with ID: {}", id);

            Optional<EmployeeSuggestion> suggestionOpt = employeeSuggestionRepository.findById(id);

            if (suggestionOpt.isPresent()) {
                employeeSuggestionRepository.deleteById(id);
                log.info("Successfully deleted employee suggestion with ID: {}", id);

                Map<String, Object> responseData = new HashMap<>();
                responseData.put("id", id);
                responseData.put("message", "Employee suggestion deleted successfully");

                return responseUtil.wrapSuccess(responseData, HttpStatus.OK);
            } else {
                return responseUtil.wrapError("Employee suggestion not found",
                        "No suggestion found with ID: " + id, HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            log.error("Error deleting employee suggestion with ID {}: {}", id, e.getMessage(), e);
            return responseUtil.wrapError("Failed to delete employee suggestion",
                    e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
