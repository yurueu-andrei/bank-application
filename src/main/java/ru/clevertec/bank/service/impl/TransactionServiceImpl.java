package ru.clevertec.bank.service.impl;

import ru.clevertec.bank.dto.TransactionRequestDto;
import ru.clevertec.bank.dto.TransactionResponseDto;
import ru.clevertec.bank.exception.EntityNotFoundException;
import ru.clevertec.bank.mapper.TransactionMapper;
import ru.clevertec.bank.entity.Transaction;
import ru.clevertec.bank.repository.TransactionRepository;
import ru.clevertec.bank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;

/**
 * Service implementation for managing transactions.
 *
 * @author Andrei Yuryeu
 */
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);

    /**
     * Finds a transaction by its unique identifier (ID).
     *
     * @param id The unique identifier of the transaction.
     * @return The TransactionResponseDto representing the found transaction.
     * @throws EntityNotFoundException If no transaction with the given ID is found.
     */
    @Override
    public TransactionResponseDto findById(Long id) {
        return transactionMapper.toDto(
                transactionRepository.findById(id).orElseThrow(() ->
                        new EntityNotFoundException("Transaction with id = " + id + " was not found")));
    }

    /**
     * Retrieves a list of transactions with pagination support.
     *
     * @param limit  The maximum number of transactions to retrieve.
     * @param offset The starting index for pagination.
     * @return A list of TransactionResponseDto representing transactions.
     */
    @Override
    public List<TransactionResponseDto> findAll(int limit, int offset) {
        return transactionMapper.toListOfDto(transactionRepository.findAll(limit, offset));
    }

    /**
     * Retrieves a list of transactions for a specific account within a given date range.
     *
     * @param number    The account number associated with the transactions.
     * @param startDate The start date of the period.
     * @param endDate   The end date of the period.
     * @return A list of TransactionResponseDto representing transactions within the specified date range.
     */
    @Override
    public List<TransactionResponseDto> findAllForPeriod(String number, LocalDate startDate, LocalDate endDate) {
        return transactionMapper.toListOfDto(transactionRepository.findAllForPeriod(number, startDate, endDate));
    }

    /**
     * Saves a new transaction based on the provided TransactionRequestDto.
     *
     * @param transactionDto The TransactionRequestDto containing transaction information.
     * @return The TransactionResponseDto representing the saved transaction.
     */
    @Override
    public TransactionResponseDto save(TransactionRequestDto transactionDto) {
        Transaction transaction = transactionMapper.fromDto(transactionDto);
        return transactionMapper.toDto(transactionRepository.save(transaction));
    }
}
