package ru.clevertec.bank.service;

import ru.clevertec.bank.dto.TransactionRequestDto;
import ru.clevertec.bank.dto.TransactionResponseDto;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing transactions.
 *
 * @author Andrei Yuryeu
 */
public interface TransactionService {

    TransactionResponseDto findById(Long id);

    List<TransactionResponseDto> findAll(int limit, int offset);

    List<TransactionResponseDto> findAllForPeriod(String number, LocalDate from, LocalDate to);

    TransactionResponseDto save(TransactionRequestDto transactionDto);
}
