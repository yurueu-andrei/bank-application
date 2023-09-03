package ru.clevertec.bank.dto;

/**
 * A data transfer object (DTO) representing a response containing information about a bank.
 * This DTO includes details such as the bank's ID and name.
 * It is used to transfer data from the server to the client when retrieving bank information.
 *
 * @author Andrei Yuryeu
 */
public record BankResponseDto(
        Long id,
        String name
) {
}
