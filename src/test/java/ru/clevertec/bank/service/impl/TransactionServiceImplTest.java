package ru.clevertec.bank.service.impl;

import ru.clevertec.bank.dto.TransactionRequestDto;
import ru.clevertec.bank.dto.TransactionResponseDto;
import ru.clevertec.bank.exception.EntityNotFoundException;
import ru.clevertec.bank.entity.Transaction;
import ru.clevertec.bank.repository.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void findAllTest_shouldReturnTransactionsWrappedIntoResponseDto() {
        //given
        BigDecimal amount = new BigDecimal(100);
        LocalDateTime now = LocalDateTime.now();
        Transaction transaction1 = new Transaction(amount, "WITHDRAW", "USD", 1L, 3L, now);
        transaction1.setId(3L);
        Transaction transaction2 = new Transaction(amount, "WITHDRAW", "USD", 1L, 3L, now);
        transaction2.setId(4L);

        List<Transaction> transactions = new ArrayList<>() {{
            add(transaction1);
            add(transaction2);
        }};
        List<TransactionResponseDto> expected = transactions.stream()
                .map(tr -> new TransactionResponseDto(tr.getId(),
                        tr.getAmount(), tr.getType(), tr.getCurrency(),
                        tr.getSenderAccountId(), tr.getReceiverAccountId(), tr.getCreatedDate()))
                .toList();

        //when
        when(transactionRepository.findAll(2, 2)).thenReturn(transactions);
        List<TransactionResponseDto> actual = transactionService.findAll(2, 2);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllForPeriodTest_shouldReturnTransactionsWrappedIntoResponseDtoBetweenGivenDates() {
        //given
        BigDecimal amount = new BigDecimal(100);
        LocalDateTime now = LocalDateTime.now();
        Transaction transaction1 = new Transaction(amount, "WITHDRAW", "USD",
                1L, 3L, now);
        transaction1.setId(3L);
        Transaction transaction2 = new Transaction(amount, "WITHDRAW", "USD",
                1L, 3L, now);
        transaction2.setId(4L);

        List<Transaction> transactions = new ArrayList<>() {{
            add(transaction1);
            add(transaction2);
        }};
        List<TransactionResponseDto> expected = transactions.stream()
                .map(tr -> new TransactionResponseDto(tr.getId(),
                        tr.getAmount(), tr.getType(), tr.getCurrency(),
                        tr.getSenderAccountId(), tr.getReceiverAccountId(), tr.getCreatedDate()))
                .toList();

        //when
        when(transactionRepository.findAllForPeriod("1111111111111111",
                LocalDate.now().minusMonths(1), LocalDate.now())).thenReturn(transactions);
        List<TransactionResponseDto> actual = transactionService.findAllForPeriod("1111111111111111",
                LocalDate.now().minusMonths(1), LocalDate.now());

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void saveTest_shouldReturnUserResponseDtoWithId5() {
        //given
        Long id = 5L;
        BigDecimal amount = new BigDecimal(100);
        LocalDateTime now = LocalDateTime.now();
        Transaction transactionWithoutId = new Transaction(amount, "WITHDRAW", "USD",
                1L, 3L, null);
        Transaction transactionWithId = new Transaction(amount, "WITHDRAW", "USD",
                1L, 3L, now);
        transactionWithId.setId(id);

        TransactionRequestDto requestDto = new TransactionRequestDto(amount, "WITHDRAW", "USD",
                1L, 3L);
        TransactionResponseDto expected = new TransactionResponseDto(id, amount, "WITHDRAW", "USD",
                1L, 3L, now);

        //when
        when(transactionRepository.save(transactionWithoutId)).thenReturn(transactionWithId);
        TransactionResponseDto actual = transactionService.save(requestDto);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Nested
    class FindById {

        @Test
        void findByIdTest_shouldReturnUserResponseDtoWithId5() {
            //given
            Long id = 5L;
            BigDecimal amount = new BigDecimal(100);
            LocalDateTime now = LocalDateTime.now();
            TransactionResponseDto expected = new TransactionResponseDto(id, amount, "WITHDRAW", "USD",
                    1L, 3L, now);
            Transaction transaction = new Transaction(amount, "WITHDRAW", "USD",
                    1L, 3L, now);
            transaction.setId(id);

            //when
            when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
            TransactionResponseDto actual = transactionService.findById(id);

            //then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        void findByIdTest_shouldThrowEntityNotFoundExceptionForNonExistentTransaction() {
            //given
            Long id = 100L;

            //when
            when(transactionRepository.findById(id)).thenReturn(Optional.empty());

            //then
            Assertions.assertThrows(EntityNotFoundException.class, () -> transactionService.findById(id));
        }
    }
}