package com.nexora.backend.authentication.service.impl;

import com.nexora.backend.domain.request.AuthenticationRequest;
import com.nexora.backend.domain.request.RegistrationRequest;
import com.nexora.backend.domain.response.APIResponse;
import com.nexora.backend.domain.response.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface AuthenticationService {


    ResponseEntity<APIResponse> findDriverEmailByDriverId(String email) throws IOException;

    /**
     * Registers a new user based on the provided registration request.
     *
     * @param registrationRequest the request object containing user registration details
     * @return an {@link AuthenticationResponse} containing authentication details for the newly registered user
     */
    AuthenticationResponse register(RegistrationRequest registrationRequest);

    /**
     * Authenticates a user based on the provided authentication request.
     *
     * @param request the request object containing user authentication credentials
     * @return an {@link AuthenticationResponse} containing authentication details upon successful authentication
     */
    AuthenticationResponse authenticate(AuthenticationRequest request);

    /**
     * Refreshes an authentication token using the provided HTTP request and response objects.
     *
     * @param request  the {@link HttpServletRequest} containing the refresh token
     * @param response the {@link HttpServletResponse} to write the refreshed token response
     * @throws IOException if an I/O error occurs during token refresh
     */
    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * Logs out a user by invalidating their authentication token.
     *
     * @param request  the {@link HttpServletRequest} containing the token to be invalidated
     * @param response the {@link HttpServletResponse} to write the logout response
     * @throws IOException if an I/O error occurs during logout
     */
    void logout(HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * Retrieves a paginated list of all authentication records.
     *
     * @param page the page number to retrieve (zero-based)
     * @param size the number of records per page
     * @return a {@link ResponseEntity} containing an {@link APIResponse} with the authentication records
     */
    ResponseEntity<APIResponse> getAllAuthentications(int page, int size);
}
