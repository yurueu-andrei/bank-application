package ru.clevertec.bank.dto;

/**
 * A data transfer object (DTO) representing a request to create or update an account.
 * This DTO contains information about the account, including its number, currency, user ID, and bank ID.
 * It is used to transfer data between the client and server when creating or updating accounts.
 *
 * @author Andrei Yuryeu
 */
public record AccountRequestDto(
        String number,
        String currency,
        Long userId,
        Long bankId
) {
}
