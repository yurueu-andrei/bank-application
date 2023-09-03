package ru.clevertec.bank.service.impl;

import ru.clevertec.bank.dto.BankRequestDto;
import ru.clevertec.bank.dto.BankResponseDto;
import ru.clevertec.bank.exception.EntityNotFoundException;
import ru.clevertec.bank.entity.Bank;
import ru.clevertec.bank.repository.BankRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankServiceImplTest {

    @Mock
    private BankRepository bankRepository;

    @InjectMocks
    private BankServiceImpl bankService;

    @Test
    void findAllTest_shouldReturnBanksWrappedIntoResponseDto() {
        //given
        Bank bank1 = new Bank("Bank1", true);
        bank1.setId(3L);
        Bank bank2 = new Bank("Bank2", true);
        bank2.setId(4L);

        List<Bank> banks = new ArrayList<>() {{
            add(bank1);
            add(bank2);
        }};
        List<BankResponseDto> expected = banks.stream()
                .map(b -> new BankResponseDto(b.getId(), b.getName()))
                .toList();

        //when
        when(bankRepository.findAll(2, 2)).thenReturn(banks);
        List<BankResponseDto> actual = bankService.findAll(2, 2);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void saveTest_shouldReturnBankResponseDtoWithId5() {
        //given
        Long id = 5L;
        Bank bankWithoutId = new Bank("Bank", null);
        Bank bankWithId = new Bank("Bank", true);
        bankWithId.setId(id);

        BankRequestDto requestDto = new BankRequestDto("Bank");
        BankResponseDto expected = new BankResponseDto(id, "Bank");

        //when
        when(bankRepository.save(bankWithoutId)).thenReturn(bankWithId);
        BankResponseDto actual = bankService.save(requestDto);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Nested
    class FindById {

        @Test
        void findByIdTest_shouldReturnBankResponseDtoWithId1() {
            //given
            Long id = 1L;
            BankResponseDto expected = new BankResponseDto(id, "Bank");
            Bank bank = new Bank("Bank", true);
            bank.setId(id);

            //when
            when(bankRepository.findById(id)).thenReturn(Optional.of(bank));
            BankResponseDto actual = bankService.findById(id);

            //then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        void findByIdTest_shouldThrowEntityNotFoundExceptionForNonExistentBank() {
            //given
            Long id = 100L;

            //when
            when(bankRepository.findById(id)).thenReturn(Optional.empty());

            //then
            Assertions.assertThrows(EntityNotFoundException.class, () -> bankService.findById(id));
        }
    }

    @Nested
    class FindByAccountId {

        @Test
        void findByAccountIdTest_shouldReturnBankResponseDtoWithId1() {
            //given
            Long id = 1L;
            BankResponseDto expected = new BankResponseDto(id, "Bank");
            Bank bank = new Bank("Bank", true);
            bank.setId(1L);

            //when
            when(bankRepository.findByAccountId(id)).thenReturn(Optional.of(bank));
            BankResponseDto actual = bankService.findByAccountId(id);

            //then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        void findByAccountIdTest_shouldThrowEntityNotFoundExceptionForWrongAccountId() {
            //given
            Long id = 100L;

            //when
            when(bankRepository.findByAccountId(id)).thenReturn(Optional.empty());

            //then
            Assertions.assertThrows(EntityNotFoundException.class, () -> bankService.findByAccountId(id));
        }
    }

    @Nested
    class Update {

        @Test
        void updateTest_shouldReturnTrueInCaseOfSuccessfulUpdate() {
            //given
            Long id = 3L;
            BankRequestDto requestDto = new BankRequestDto("NEWBank");
            Bank bankWithId = new Bank("Bank", true);
            bankWithId.setId(id);
            Bank bank = new Bank("NEWBank", null);

            //when
            when(bankRepository.findById(id)).thenReturn(Optional.of(bankWithId));
            when(bankRepository.update(bank)).thenReturn(true);
            boolean actual = bankService.update(id, requestDto);

            //then
            Assertions.assertTrue(actual);
        }

        @Test
        void updateTest_shouldThrowExceptionIfBankIsNotFound() {
            //given
            Long id = 3L;
            BankRequestDto requestDto = new BankRequestDto("NEWBank");

            //when
            when(bankRepository.findById(id)).thenReturn(Optional.empty());

            //then
            Assertions.assertThrows(EntityNotFoundException.class, () -> bankService.update(id, requestDto));
        }
    }

    @Nested
    class Delete {

        @Test
        void deleteTest_shouldDeleteBankWithId3() {
            //given
            Long id = 3L;
            Bank bankWithId = new Bank("Bank", true);
            bankWithId.setId(id);

            //when
            when(bankRepository.findById(id)).thenReturn(Optional.of(bankWithId));
            when(bankRepository.delete(id)).thenReturn(true);
            boolean actual = bankService.delete(id);

            //then
            Assertions.assertTrue(actual);
            verify(bankRepository, times(1)).delete(id);
        }

        @Test
        void deleteTest_shouldThrowExceptionIfBankIsNotFound() {
            //given
            Long id = 3L;

            //when
            when(bankRepository.findById(id)).thenReturn(Optional.empty());

            //then
            Assertions.assertThrows(EntityNotFoundException.class, () -> bankService.delete(id));
        }
    }
}