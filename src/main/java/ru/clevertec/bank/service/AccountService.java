package ru.clevertec.bank.service;

import ru.clevertec.bank.dto.AccountRequestDto;
import ru.clevertec.bank.dto.AccountResponseDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for managing accounts.
 *
 * @author Andrei Yuryeu
 */
public interface AccountService {

    void applyPercentage();

    AccountResponseDto findById(Long id);

    AccountResponseDto findByNumber(String number);

    List<AccountResponseDto> findAll(int limit, int offset);

    AccountResponseDto save(AccountRequestDto account);

    boolean delete(Long id);

    boolean withdraw(String number, BigDecimal amount);

    boolean deposit(String number, BigDecimal amount);

    boolean transfer(String senderNumber, String receiverNumber, BigDecimal amount);
}
