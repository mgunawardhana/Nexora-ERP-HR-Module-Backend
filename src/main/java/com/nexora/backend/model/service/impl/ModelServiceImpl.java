package com.nexora.backend.model.service.impl;

import com.nexora.backend.authentication.repository.EmployeeDetailsRepository;
import com.nexora.backend.domain.entity.EmployeeDetails;
import com.nexora.backend.model.service.ModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    @NonNull
    private final EmployeeDetailsRepository employeeDetailsRepository;


    @Override
    public Optional<EmployeeDetails> generateSuggestionRelatedToEmployee(String id) {

        Optional<EmployeeDetails> byUserId = employeeDetailsRepository.findByUserId(1);
        if (byUserId.isPresent()) {
            System.out.println(byUserId);
        }
        return byUserId;
    }
}
