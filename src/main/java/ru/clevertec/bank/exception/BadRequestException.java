package ru.clevertec.bank.exception;

/**
 * An exception that indicates a bad or invalid request.
 * This exception is typically thrown when a client sends a request that cannot be processed due to issues
 * such as missing or invalid parameters.
 *
 * @author Andrei Yuryeu
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
