package ru.clevertec.bank.dto;

import java.time.LocalDate;

/**
 * A data transfer object (DTO) representing a response containing information about a user.
 * This DTO includes details such as the user's ID, name, surname, and birthdate.
 * It is used to transfer data from the server to the client when retrieving user information.
 *
 * @author Andrei Yuryeu
 */
public record UserResponseDto(
        Long id,
        String name,
        String surname,
        LocalDate birthdate
) {
}
