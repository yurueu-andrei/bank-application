package ru.clevertec.bank.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A data transfer object (DTO) representing a response containing information about an account.
 * This DTO includes details such as the account's ID, number, balance, currency, user ID, bank ID,
 * and the date when the account was created.
 * It is used to transfer data from the server to the client when retrieving account information.
 *
 * @author Andrei Yuryeu
 */
public record AccountResponseDto(
        Long id,
        String number,
        BigDecimal balance,
        String currency,
        Long userId,
        Long bankId,
        LocalDate createdDate
) {
}
