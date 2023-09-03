package ru.clevertec.bank.service.impl;

import ru.clevertec.bank.dto.BankRequestDto;
import ru.clevertec.bank.dto.BankResponseDto;
import ru.clevertec.bank.exception.EntityNotFoundException;
import ru.clevertec.bank.mapper.BankMapper;
import ru.clevertec.bank.entity.Bank;
import ru.clevertec.bank.repository.BankRepository;
import ru.clevertec.bank.service.BankService;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Service implementation for managing banks.
 *
 * @author Andrei Yuryeu
 */
public class BankServiceImpl implements BankService {

    private final BankRepository bankRepository;

    private final BankMapper bankMapper = Mappers.getMapper(BankMapper.class);

    public BankServiceImpl(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    /**
     * Finds a bank by its unique identifier (ID).
     *
     * @param id The unique identifier of the bank.
     * @return The BankResponseDto representing the found bank.
     * @throws EntityNotFoundException If no bank with the given ID is found.
     */
    @Override
    public BankResponseDto findById(Long id) {
        return bankMapper.toDto(
                bankRepository.findById(id).orElseThrow(() ->
                        new EntityNotFoundException("Bank with id = " + id + " was not found")));
    }

    /**
     * Finds a bank by its associated account's unique identifier (ID).
     *
     * @param accountId The unique identifier of the associated account.
     * @return The BankResponseDto representing the found bank.
     * @throws EntityNotFoundException If no bank associated with the given account ID is found.
     */
    @Override
    public BankResponseDto findByAccountId(Long accountId) {
        return bankMapper.toDto(
                bankRepository.findByAccountId(accountId).orElseThrow(() ->
                        new EntityNotFoundException("User with account id = " + accountId + " was not found")));
    }

    /**
     * Retrieves a list of banks with pagination support.
     *
     * @param limit  The maximum number of banks to retrieve.
     * @param offset The starting index for pagination.
     * @return A list of BankResponseDto representing banks.
     */
    @Override
    public List<BankResponseDto> findAll(int limit, int offset) {
        return bankMapper.toListOfDto(bankRepository.findAll(limit, offset));
    }

    /**
     * Saves a new bank based on the provided BankRequestDto.
     *
     * @param bankDto The BankRequestDto containing bank information.
     * @return The BankResponseDto representing the saved bank.
     */
    @Override
    public BankResponseDto save(BankRequestDto bankDto) {
        Bank bank = bankMapper.fromDto(bankDto);
        return bankMapper.toDto(bankRepository.save(bank));
    }

    /**
     * Updates an existing bank based on the provided ID and BankRequestDto.
     *
     * @param id      The unique identifier of the bank to update.
     * @param bankDto The BankRequestDto containing updated bank information.
     * @return True if the bank was successfully updated, otherwise false.
     * @throws EntityNotFoundException If no bank with the given ID is found.
     */
    @Override
    public boolean update(Long id, BankRequestDto bankDto) {
        bankRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Bank with id = " + id + " was not found"));
        Bank bank = bankMapper.fromDto(bankDto);
        bank.setId(id);
        return bankRepository.update(bank);
    }

    /**
     * Deletes a bank by its unique identifier (ID).
     *
     * @param id The unique identifier of the bank to delete.
     * @return True if the bank was successfully deleted, otherwise false.
     * @throws EntityNotFoundException If no bank with the given ID is found.
     */
    @Override
    public boolean delete(Long id) {
        bankRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Bank with id = " + id + " was not found"));
        return bankRepository.delete(id);
    }
}
