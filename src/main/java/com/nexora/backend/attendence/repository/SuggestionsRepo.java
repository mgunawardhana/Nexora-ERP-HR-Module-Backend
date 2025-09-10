package com.nexora.backend.attendence.repository;


import com.nexora.backend.domain.entity.EmployeeSuggestion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SuggestionsRepo extends CrudRepository<EmployeeSuggestion, Integer> {
}
