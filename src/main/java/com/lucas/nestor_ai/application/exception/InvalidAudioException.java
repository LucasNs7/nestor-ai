package com.lucas.nestor_ai.application.exception;

public class InvalidAudioException extends RuntimeException {
    public InvalidAudioException(String message) {
        super(message);
    }

    public InvalidAudioException(String message, Throwable cause) {
        super(message, cause);
    }
}
