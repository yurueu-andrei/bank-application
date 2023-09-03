package ru.clevertec.bank.dto;

/**
 * A data transfer object (DTO) representing a request to create a new bank.
 * This DTO includes details such as the bank's name.
 * It is used to transfer data from the client to the server when creating a new bank.
 *
 * @author Andrei Yuryeu
 */
public record BankRequestDto(
        String name
) {
}
