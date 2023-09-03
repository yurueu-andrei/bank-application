package ru.clevertec.bank.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents an Account entity with properties such as number, balance, currency, userId,
 * bankId, createdDate, and active status.
 * Extends the BaseEntity class to inherit the 'id' field as a unique identifier.
 *
 * @author Yurkova Anastacia
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Account extends BaseEntity {

    private String number;
    private BigDecimal balance;
    private String currency;
    private Long userId;
    private Long bankId;
    private LocalDate createdDate;
    private Boolean active;
}
