package ru.clevertec.bank.dto;

import java.math.BigDecimal;

/**
 * A data transfer object (DTO) representing a request to create a new transaction.
 * This DTO includes details such as the transaction's amount, type, currency,
 * sender account ID, and receiver account ID.
 * It is used to transfer data from the client to the server when creating a new transaction.
 *
 * @author Andrei Yuryeu
 */
public record TransactionRequestDto(
        BigDecimal amount,
        String type,
        String currency,
        Long senderAccountId,
        Long receiverAccountId
) {
}
