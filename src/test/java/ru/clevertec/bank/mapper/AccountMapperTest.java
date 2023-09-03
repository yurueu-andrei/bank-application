package ru.clevertec.bank.mapper;

import ru.clevertec.bank.dto.AccountRequestDto;
import ru.clevertec.bank.dto.AccountResponseDto;
import ru.clevertec.bank.entity.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class AccountMapperTest {

    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    @Test
    void toDtoTest_shouldMapAccountToAccountResponseDto() {
        //given
        Account account = new Account("0123400100000001", BigDecimal.valueOf(49734.62), "USD", 2L, 2L, LocalDate.of(2013, 11, 12), true);
        account.setId(3L);

        var expected = new AccountResponseDto(account.getId(), account.getNumber(), account.getBalance(),
                account.getCurrency(), account.getUserId(), account.getBankId(), account.getCreatedDate());

        //when
        AccountResponseDto actual = accountMapper.toDto(account);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void fromDtoTest_shouldMapAccountRequestDtoIntoAccount() {
        //given
        AccountRequestDto accountRequestDto = new AccountRequestDto("0123400100000001", "USD", 2L, 2L);

        var expected = new Account(accountRequestDto.number(), null, accountRequestDto.currency(), accountRequestDto.bankId(),
                accountRequestDto.bankId(), null, null);
        expected.setId(3L);

        //when
        Account actual = accountMapper.fromDto(accountRequestDto);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void toListOfDtoTest_shouldMapListOfAccountsIntoListOfAccountResponseDto() {
        //given
        Account account1 = new Account("0123400100000001", BigDecimal.valueOf(49734.62), "USD", 2L, 2L, LocalDate.of(2013, 11, 12), true);
        account1.setId(3L);
        Account account2 = new Account("0104123400000001", BigDecimal.valueOf(98753.34), "EUR", 4L, 3L, LocalDate.of(2023, 12, 4), true);
        account2.setId(4L);

        List<Account> accounts = new ArrayList<>() {{
            add(account1);
            add(account2);
        }};

        List<AccountResponseDto> expected = accounts.stream().map(a -> new AccountResponseDto(a.getId(), a.getNumber(),
                a.getBalance(), a.getCurrency(), a.getUserId(), a.getBankId(), a.getCreatedDate())).toList();

        //when
        List<AccountResponseDto> actual = accountMapper.toListOfDto(accounts);

        //then
        Assertions.assertEquals(expected, actual);
    }
}
