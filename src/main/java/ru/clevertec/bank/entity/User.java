package ru.clevertec.bank.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Represents a User entity with properties such as name, surname, birthdate, and active status.
 * Extends the BaseEntity class to inherit the 'id' field as a unique identifier.
 *
 * @author Andrei Yuryeu
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User extends BaseEntity {

    private String name;
    private String surname;
    private LocalDate birthdate;
    private Boolean active;
}
