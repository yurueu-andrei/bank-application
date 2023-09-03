package ru.clevertec.bank.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Base entity class representing common properties for all entities.
 * It includes an 'id' field as a unique identifier for the entity.
 *
 * @author Andrei Yuryeu
 */
@NoArgsConstructor
@Data
public abstract class BaseEntity {

    private Long id;
}
