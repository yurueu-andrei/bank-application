package ru.clevertec.bank.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A data transfer object (DTO) representing a response containing information about a transaction.
 * This DTO includes details such as the transaction's ID, amount, type, currency, sender account ID,
 * receiver account ID, and the date when the transaction was created.
 * It is used to transfer data from the server to the client when retrieving transaction information.
 *
 * @author Andrei Yuryeu
 */
public record TransactionResponseDto(
        Long id,
        BigDecimal amount,
        String type,
        String currency,
        Long senderAccountId,
        Long receiverAccountId,
        LocalDateTime createdDate
) {
}
