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

    @Override
    @Transactional
    public ResponseEntity<APIResponse> saveEmployeeSuggestion(EmployeeSuggestionRequest request) {
        try {
            log.info("Saving employee suggestion for: {} {}", request.getFirstName(), request.getLastName());

            // Log the incoming data (similar to console.log in frontend)
            log.info("=== SAVING EMPLOYEE SUGGESTION DATA ===");
            log.info("Employee Data: {}", Map.of(
                    "First Name", request.getFirstName() != null ? request.getFirstName() : "N/A",
                    "Last Name", request.getLastName() != null ? request.getLastName() : "N/A",
                    "Full Name", request.getFullName() != null ? request.getFullName() :
                            String.format("%s %s", request.getFirstName(), request.getLastName()).trim(),
                    "Department", request.getDepartment() != null ? request.getDepartment() : "N/A",
                    "Employee Code", request.getEmployeeCode() != null ? request.getEmployeeCode() : "N/A"
            ));
            log.info("Suggestion: {}", request.getSuggestion());
            log.info("Timestamp: {}", request.getTimestamp() != null ? request.getTimestamp() : LocalDateTime.now());

            // Create entity from request
            EmployeeSuggestion suggestion = EmployeeSuggestion.builder()
                    .firstName(request.getFirstName() != null ? request.getFirstName().trim() : "")
                    .lastName(request.getLastName() != null ? request.getLastName().trim() : "")
                    .fullName(request.getFullName() != null ? request.getFullName().trim() : null)
                    .department(request.getDepartment() != null ? request.getDepartment().trim() : null)
                    .employeeCode(request.getEmployeeCode() != null ? request.getEmployeeCode().trim() : null)
                    .suggestion(request.getSuggestion() != null ? request.getSuggestion().trim() : "")
                    .createdAt(request.getTimestamp() != null ? request.getTimestamp() : LocalDateTime.now())
                    .build();

            // Save to database
            EmployeeSuggestion savedSuggestion = employeeSuggestionRepository.save(suggestion);

            log.info("Successfully saved employee suggestion with ID: {}", savedSuggestion.getId());
            log.info("Complete Data Object: {}", savedSuggestion);
            log.info("=======================================");

            // Create response data
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("id", savedSuggestion.getId());
            responseData.put("firstName", savedSuggestion.getFirstName());
            responseData.put("lastName", savedSuggestion.getLastName());
            responseData.put("fullName", savedSuggestion.getFullName());
            responseData.put("department", savedSuggestion.getDepartment());
            responseData.put("employeeCode", savedSuggestion.getEmployeeCode());
            responseData.put("suggestion", savedSuggestion.getSuggestion());
            responseData.put("createdAt", savedSuggestion.getCreatedAt());
            responseData.put("updatedAt", savedSuggestion.getUpdatedAt());
            responseData.put("message", "Employee suggestion saved successfully");

            return responseUtil.wrapSuccess(responseData, HttpStatus.CREATED);

        } catch (Exception e) {
            log.error("Error saving employee suggestion for {} {}: {}",
                    request.getFirstName(), request.getLastName(), e.getMessage(), e);
            return responseUtil.wrapError("Failed to save employee suggestion",
                    e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
