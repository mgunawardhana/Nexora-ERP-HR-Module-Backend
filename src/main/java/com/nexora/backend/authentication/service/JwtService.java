package com.nexora.backend.authentication.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface JwtService {

    /**
     * Extracts the username from a given JWT token.
     *
     * @param token the JWT token to extract the username from
     * @return the username contained within the token
     */
    String extractUserName(String token);

    /**
     * Extracts a specific claim from a JWT token using a provided claims resolver function.
     *
     * @param <T> the type of the claim to be extracted
     * @param token the JWT token to extract the claim from
     * @param claimsResolver a {@link Function} that resolves the desired claim from the {@link Claims} object
     * @return the extracted claim of type T
     */
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    /**
     * Generates a JWT token for the given user details.
     *
     * @param userDetails the {@link UserDetails} object containing user information
     * @return the generated JWT token as a string
     */
    String generateToken(UserDetails userDetails);

    /**
     * Generates a JWT token with additional claims for the given user details.
     *
     * @param extraClaims a {@link Map} containing additional claims to include in the token
     * @param userDetails the {@link UserDetails} object containing user information
     * @return the generated JWT token with extra claims as a string
     */
    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    /**
     * Generates a refresh token for the given user details.
     *
     * @param userDetails the {@link UserDetails} object containing user information
     * @return the generated refresh token as a string
     */
    String generateRefreshToken(UserDetails userDetails);

    /**
     * Validates a JWT token against the provided user details.
     *
     * @param token the JWT token to validate
     * @param userDetails the {@link UserDetails} object to validate the token against
     * @return true if the token is valid for the user, false otherwise
     */
    boolean isTokenValidated(String token, UserDetails userDetails);
}
