package com.nexora.backend.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 * This is a runtime exception indicating that an operation failed due to a missing resource.
 */
class ResourceNotFoundException extends RuntimeException {
    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining why the resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}