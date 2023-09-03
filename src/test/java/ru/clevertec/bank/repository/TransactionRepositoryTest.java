package ru.clevertec.bank.repository;

import ru.clevertec.bank.exception.RepositoryException;
import ru.clevertec.bank.entity.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class TransactionRepositoryTest extends BaseRepositoryTest {

    private final TransactionRepository transactionRepository = new TransactionRepository(dataSource);

    @Nested
    class FindAll {

        @Test
        void findAllTest_shouldReturnTransactionsWithId1And2() {
            //given
            Transaction transaction1 = new Transaction(
                    BigDecimal.valueOf(100.11), "WITHDRAW", "BYN", 0L, 2L,
                    LocalDateTime.of(2023, 12, 18, 12, 11, 7, 0));
            transaction1.setId(1L);
            Transaction transaction2 = new Transaction(
                    BigDecimal.valueOf(123.22), "WITHDRAW", "RUB", 0L, 1L,
                    LocalDateTime.of(2023, 9, 22, 10, 11, 7, 0));
            transaction2.setId(2L);

            List<Transaction> expected = new ArrayList<>() {{
                add(transaction1);
                add(transaction2);
            }};

            //when
            List<Transaction> actual = transactionRepository.findAll(2, 0);

            //then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        void findAllForPeriodTest_shouldReturnTransactionsForAccountWithNumber0104100100000001BetweenTwoDates() {
            //given
            Transaction transaction1 = new Transaction(
                    BigDecimal.valueOf(567.55), "TRANSFER", "EUR", 5L, 1L,
                    LocalDateTime.of(2023, 12, 4, 12, 45, 7, 0));
            transaction1.setId(1L);

            List<Transaction> expected = new ArrayList<>() {{
                add(transaction1);
            }};

            //when
            List<Transaction> actual = transactionRepository.findAllForPeriod("0104100100000001",
                    LocalDate.of(2023, 12, 1),
                    LocalDate.of(2023, 12, 31));

            //then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Test
    void updateTest_shouldUpdateTransactionWithId6AndReturnTrue() {
        //given
        Long id = 6L;
        Transaction expected = new Transaction(
                BigDecimal.valueOf(12.66), "TRANSFER", "USD", 2L, 4L,
                null);
        expected.setId(id);

        //when
        boolean result = transactionRepository.update(expected);

        //then
        Assertions.assertTrue(result);
    }

    @Test
    void deleteTest_shouldDeleteTransactionWithId4AndReturnTrue() {
        //given
        Long id = 4L;

        //when
        boolean result = transactionRepository.delete(id);
        Optional<Transaction> foundTransaction = transactionRepository.findById(id);

        //then
        Assertions.assertTrue(result);
        Assertions.assertFalse(foundTransaction.isPresent());
    }

    @Nested
    class FindById {

        @Test
        void findByIdTest_shouldReturnTransactionWithId1() {
            //given
            Long id = 1L;
            Transaction transaction = new Transaction(
                    BigDecimal.valueOf(100.11), "WITHDRAW", "BYN", 0L, 2L,
                    LocalDateTime.of(2023, 12, 18, 12, 11, 7, 0));
            transaction.setId(id);
            Optional<Transaction> expected = Optional.of(transaction);

            //when
            Optional<Transaction> actual = transactionRepository.findById(id);

            //then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        void findByIdTest_shouldReturnEmptyOptional() {
            //given
            Long id = 100L;

            Optional<Transaction> expected = Optional.empty();

            //when
            Optional<Transaction> actual = transactionRepository.findById(id);

            //then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class Save {

        @Test
        void saveTest_shouldReturnSavedTransactionAndAddTransactionToDB() {
            //given
            Transaction transaction = new Transaction(
                    BigDecimal.valueOf(4567), "REFILL", "BYN", 1L, 5L,
                    LocalDateTime.of(2023, 11, 12, 18, 15, 7, 0));

            //when
            Transaction actual = transactionRepository.save(transaction);

            //then
            Assertions.assertNotNull(actual);
        }


        @Test
        void saveTest_shouldThrowExceptionIfAnyFieldIsNull() {
            //given
            Transaction transactionWithoutId = new Transaction(
                    BigDecimal.valueOf(1000.33), null, "BYN", 1L, 5L,
                    LocalDateTime.of(2023, 11, 12, 18, 15, 7, 0));

            //then
            Assertions.assertThrows(RepositoryException.class, () -> transactionRepository.save(transactionWithoutId));
        }
    }
}