package com.nexora.backend.authentication.repository;

import com.nexora.backend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities.
 * Extends {@link JpaRepository} to provide basic CRUD operations and additional query methods.
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * Retrieves a {@link User} entity by their email address.
     *
     * @param email the email address to search for
     * @return an {@link Optional} containing the found {@link User} if exists, or an empty {@link Optional} if no user is found
     */
    Optional<User> findByEmail(String email);
}
