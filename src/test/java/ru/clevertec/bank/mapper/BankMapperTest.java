package ru.clevertec.bank.mapper;

import ru.clevertec.bank.dto.BankRequestDto;
import ru.clevertec.bank.dto.BankResponseDto;
import ru.clevertec.bank.entity.Bank;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

class BankMapperTest {

    private final BankMapper bankMapper = Mappers.getMapper(BankMapper.class);

    @Test
    void toDtoTest_shouldMapBankToBankResponseDto() {
        //given
        Bank bank = new Bank("CleverBank", true);
        bank.setId(1L);
        var expected = new BankResponseDto(bank.getId(), bank.getName());

        //when
        BankResponseDto actual = bankMapper.toDto(bank);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void fromDtoTest_shouldMapBankRequestDtoIntoBank() {
        //given
        BankRequestDto bankRequestDto = new BankRequestDto("CleverBank");
        var expected = new Bank(bankRequestDto.name(), null);
        expected.setId(1L);

        //when
        Bank actual = bankMapper.fromDto(bankRequestDto);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void toListOfDtoTest_shouldMapListOfBanksIntoListOfBankResponseDto() {
        //given
        Bank bank1 = new Bank("CleverBank", true);
        bank1.setId(1L);
        Bank bank2 = new Bank("OtherBank1", true);
        bank2.setId(2L);

        List<Bank> banks = new ArrayList<>() {{
            add(bank1);
            add(bank2);
        }};

        List<BankResponseDto> expected = banks.stream().map(b -> new BankResponseDto(b.getId(), b.getName())).toList();

        //when
        List<BankResponseDto> actual = bankMapper.toListOfDto(banks);

        //then
        Assertions.assertEquals(expected, actual);
    }
}
