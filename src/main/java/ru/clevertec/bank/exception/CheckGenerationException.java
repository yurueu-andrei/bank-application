package ru.clevertec.bank.exception;

/**
 * An exception that indicates an error during check generation.
 * This exception is thrown when there are issues with generating checks or related processes.
 *
 * @author Andrei Yuryeu
 */
public class CheckGenerationException extends RuntimeException {

    public CheckGenerationException(String message) {
        super(message);
    }
}
