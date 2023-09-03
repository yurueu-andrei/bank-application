package ru.clevertec.bank.dto;

import java.time.LocalDate;

/**
 * A data transfer object (DTO) representing a request to create a new user.
 * This DTO includes details such as the user's name, surname, and birthdate.
 * It is used to transfer data from the client to the server when creating a new user.
 *
 * @author Andrei Yuryeu
 */
public record UserRequestDto(
        String name,
        String surname,
        LocalDate birthdate
) {
}
