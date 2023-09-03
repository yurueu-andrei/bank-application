package ru.clevertec.bank.mapper;

import ru.clevertec.bank.dto.AccountRequestDto;
import ru.clevertec.bank.dto.AccountResponseDto;
import ru.clevertec.bank.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Mapper interface responsible for mapping between Account entities and their corresponding DTOs.
 * It provides methods for converting Account objects to AccountResponseDto objects and vice versa.
 * Additionally, it supports mapping lists of Account entities to lists of AccountResponseDto objects.
 *
 * @author Andrei Yuryeu
 * @see Account
 * @see AccountResponseDto
 */
@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface AccountMapper {

    AccountResponseDto toDto(Account account);

    Account fromDto(AccountRequestDto dto);

    List<AccountResponseDto> toListOfDto(List<Account> accounts);
}
