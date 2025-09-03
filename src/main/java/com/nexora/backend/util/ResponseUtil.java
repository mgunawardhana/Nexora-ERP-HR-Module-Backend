package com.nexora.backend.util;

import com.nexora.backend.domain.enums.StatusCode;
import com.nexora.backend.domain.enums.StatusMessage;
import com.nexora.backend.domain.response.APIResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;

/**
 * Utility class for wrapping API responses in a standardized format.
 * Provides methods to create success and error responses with consistent structure.
 * This class is registered as a Spring component for dependency injection.
 */
@Component
public class ResponseUtil {

    private final HttpServletRequest servletRequest;

    /**
     * Constructs a new ResponseUtil instance with the provided HTTP servlet request.
     *
     * @param servletRequest the {@link HttpServletRequest} used to extract request information
     */
    public ResponseUtil(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    /**
     * Wraps a successful response in an {@link APIResponse} object with the specified value and HTTP status.
     *
     * @param value      the result object to include in the response
     * @param httpStatus the {@link HttpStatus} to set for the response
     * @return a {@link ResponseEntity} containing the constructed {@link APIResponse} with success details
     */
    public ResponseEntity<APIResponse> wrapSuccess(Object value, HttpStatus httpStatus) {
        APIResponse apiResponse = APIResponse.builder()
                .statusCode(StatusCode.SUCCESS.valueOf())
                .origin(servletRequest.getRequestURI())
                .statusMessage(StatusMessage.SUCCESS.valueOf())
                .responseTime(DateTimeUtils.format(new Date()))
                .result(value).build();
        return ResponseEntity.status(httpStatus).body(apiResponse);
    }

    /**
     * Wraps an error response in an {@link APIResponse} object with the specified value, error type, and HTTP status.
     *
     * @param value      the error details to include in the response
     * @param errorType  the type of error being reported
     * @param httpStatus the {@link HttpStatus} to set for the response
     * @return a {@link ResponseEntity} containing the constructed {@link APIResponse} with error details
     */
    public ResponseEntity<APIResponse> wrapError(Object value, String errorType, HttpStatus httpStatus) {
        APIResponse apiResponse = APIResponse.builder()
                .statusCode(StatusCode.FAILURE.valueOf())
                .statusMessage(StatusMessage.FAILURE.valueOf())
                .errorType(errorType).origin(servletRequest.getRequestURI())
                .responseTime(DateTimeUtils.format(new Date()))
                .result(Collections.singletonMap(Constant.ERROR, value))
                .build();
        return ResponseEntity.status(httpStatus).body(apiResponse);
    }
}