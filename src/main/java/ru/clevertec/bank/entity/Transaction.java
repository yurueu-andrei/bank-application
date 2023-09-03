package ru.clevertec.bank.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a Transaction entity with properties such as amount, type, currency,
 * senderAccountId, receiverAccountId, and createdDate.
 * Extends the BaseEntity class to inherit the 'id' field as a unique identifier.
 *
 * @author Andrei Yuryeu
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Transaction extends BaseEntity {

    private BigDecimal amount;
    private String type;
    private String currency;
    private Long senderAccountId;
    private Long receiverAccountId;
    private LocalDateTime createdDate;
}
