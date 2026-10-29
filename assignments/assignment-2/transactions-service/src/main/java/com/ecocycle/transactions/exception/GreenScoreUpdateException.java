package com.ecocycle.transactions.exception;

/**
 * Custom exception for green score update failures.
 * Replaces generic RuntimeException to improve error handling (Refactoring: Replace Exception with Custom Exception).
 */
public class GreenScoreUpdateException extends RuntimeException {
    
    public GreenScoreUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}

