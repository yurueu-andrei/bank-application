package ru.clevertec.bank.exception;

/**
 * An exception that indicates the entity was not found.
 * This exception is typically thrown when an operation or query attempts to access an entity that does not exist.
 *
 * @author Andrei Yuryeu
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
