package ru.clevertec.bank.service;

import ru.clevertec.bank.dto.BankRequestDto;
import ru.clevertec.bank.dto.BankResponseDto;

import java.util.List;

/**
 * Service interface for managing banks.
 *
 * @author Andrei Yuryeu
 */
public interface BankService {

    BankResponseDto findById(Long id);

    BankResponseDto findByAccountId(Long accountId);

    List<BankResponseDto> findAll(int limit, int offset);

    BankResponseDto save(BankRequestDto bank);

    boolean update(Long id, BankRequestDto bankDto);

    boolean delete(Long id);
}
