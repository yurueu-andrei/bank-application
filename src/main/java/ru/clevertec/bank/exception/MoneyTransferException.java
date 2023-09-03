package ru.clevertec.bank.exception;

public class MoneyTransferException extends RuntimeException {

    public MoneyTransferException(String message) {
        super(message);
    }
}
