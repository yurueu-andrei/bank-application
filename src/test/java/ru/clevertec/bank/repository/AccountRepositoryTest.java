package ru.clevertec.bank.repository;

import ru.clevertec.bank.exception.RepositoryException;
import ru.clevertec.bank.entity.Account;
import ru.clevertec.bank.entity.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class AccountRepositoryTest extends BaseRepositoryTest {

    private final AccountRepository accountRepository = new AccountRepository(dataSource);

    @Nested
    class FindById {

        @Test
        void findByIdTest_shouldReturnAccountWithId1() {
            //given
            Long id = 1L;
            Account expected = new Account("0104100100000001", BigDecimal.valueOf(1234.31), "BYN",
                    1L, 1L, LocalDate.of(2001, 11, 18), true);
            expected.setId(id);

            //when
            Account actual = accountRepository.findById(id).get();

            //then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        void findByIdTest_shouldReturnEmptyOptional() {
            //given
            Long id = 100L;
            Optional<Account> expected = Optional.empty();

            //when
            Optional<Account> actual = accountRepository.findById(id);

            //then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Test
    void findByNumberTest_shouldReturnAccountWithNumber0104100100000001() {
        //given
        String number = "0104100100000001";
        Account account = new Account("0104100100000001", BigDecimal.valueOf(1234.31), "BYN",
                1L, 1L, LocalDate.of(2001, 11, 18), true);
        account.setId(1L);
        Optional<Account> expected = Optional.of(account);

        //when
        Optional<Account> actual = accountRepository.findByNumber(number);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void blockingFindByNumberTest_shouldReturnAndBlockAccountWithNumber0104100100000001() {
        //given
        String number = "0104100100000001";
        Account account = new Account("0104100100000001", BigDecimal.valueOf(1234.31), "BYN",
                1L, 1L, LocalDate.of(2001, 11, 18), true);
        account.setId(1L);
        Optional<Account> expected = Optional.of(account);

        //when
        Optional<Account> actual = accountRepository.blockingFindByNumber(number);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAllTest_shouldReturnAccountsWithId3And4() {
        //given
        Account account1 = new Account("0104100100000001", BigDecimal.valueOf(1234.31), "BYN",
                1L, 1L, LocalDate.of(2001, 11, 18), true);
        account1.setId(1L);
        Account account2 = new Account("0104100100000002", BigDecimal.valueOf(500123.01), "RUB",
                3L, 1L, LocalDate.of(2022, 9, 22), true);
        account2.setId(2L);

        List<Account> expected = new ArrayList<>() {{
            add(account1);
            add(account2);
        }};

        //when
        List<Account> actual = accountRepository.findAll(2, 0);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Nested
    class Save {

        @Test
        void saveTest_shouldReturnAccountWithId7AndAddAccountToDB() {
            //given
            Account accountWithoutId = new Account("0123400100000001", BigDecimal.valueOf(0), "BYN",
                    2L, 4L, LocalDate.of(2022, 11, 12), true);
            Account expected = new Account("0123400100000001", BigDecimal.valueOf(0), "BYN",
                    2L, 4L, LocalDate.of(2022, 11, 12), true);
            expected.setId(7L);

            //when
            Account actual = accountRepository.save(accountWithoutId);

            //then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        void saveTest_shouldThrowExceptionIfAnyFieldIsNull() {
            //given
            Account accountWithoutId = new Account(null, BigDecimal.valueOf(41233.61), "BYN",
                    2L, 4L, LocalDate.of(2022, 11, 12), true);

            //then
            Assertions.assertThrows(RepositoryException.class, () -> accountRepository.save(accountWithoutId));
        }
    }

    @Nested
    class Delete {

        @Test
        void deleteTest_shouldNotDeleteAccountOfAnyBankExceptCleverBank() {
            //given
            Long id = 5L;

            //when
            boolean result = accountRepository.delete(id);
            Optional<Account> foundUser = accountRepository.findById(id);

            //then
            Assertions.assertTrue(result);
            Assertions.assertTrue(foundUser.isPresent());
        }

        @Test
        void deleteTest_shouldDeleteAccountOfCleverBank() {
            //given
            Long id = 6L;

            //when
            boolean result = accountRepository.delete(id);
            Optional<Account> foundUser = accountRepository.findById(id);

            //then
            Assertions.assertTrue(result);
            Assertions.assertFalse(foundUser.isPresent());
        }
    }

    @Test
    void withdrawTest_shouldWithdrawMoneyFromAccountWithId3() {
        //given
        BigDecimal amount = BigDecimal.valueOf(123);
        Account account = new Account("0123400100000001", BigDecimal.valueOf(49734.62), "USD",
                2L, 2L, LocalDate.of(2013, 11, 12), true);
        account.setId(3L);

        Account expectedAccount = new Account("0123400100000001", BigDecimal.valueOf(49611.62), "USD",
                2L, 2L, LocalDate.of(2013, 11, 12), true);
        expectedAccount.setId(3L);
        Transaction expectedTransaction = new Transaction(amount, "WITHDRAW", account.getCurrency(),
                null, account.getId(), null);

        //when
        Transaction transaction = accountRepository.withdraw(account, amount);
        Transaction actualTransaction = new Transaction(transaction.getAmount(), transaction.getType(),
                transaction.getCurrency(), transaction.getSenderAccountId(),
                transaction.getReceiverAccountId(), null);
        Account actualAccount = accountRepository.findByNumber("0123400100000001").get();

        //then
        Assertions.assertEquals(expectedTransaction, actualTransaction);
        Assertions.assertEquals(expectedAccount, actualAccount);
    }

    @Test
    void depositTest_shouldRefillMoneyToAccountWithId4() {
        //given
        BigDecimal amount = BigDecimal.valueOf(77);
        Account account = new Account("0104123400000001", BigDecimal.valueOf(98753.34), "EUR",
                4L, 3L, LocalDate.of(2023, 12, 4), true);
        account.setId(4L);

        Account expectedAccount = new Account("0104123400000001", BigDecimal.valueOf(98830.34), "EUR",
                4L, 3L, LocalDate.of(2023, 12, 4), true);
        expectedAccount.setId(4L);
        Transaction expectedTransaction = new Transaction(amount, "REFILL", account.getCurrency(),
                null, account.getId(), null);

        //when
        Transaction transaction = accountRepository.deposit(account, amount);
        Transaction actualTransaction = new Transaction(transaction.getAmount(), transaction.getType(),
                transaction.getCurrency(), transaction.getSenderAccountId(),
                transaction.getReceiverAccountId(), null);
        Account actualAccount = accountRepository.findById(4L).get();

        //then
        Assertions.assertEquals(expectedTransaction, actualTransaction);
        Assertions.assertEquals(expectedAccount, actualAccount);
    }

    @Test
    void transferTest_shouldTransferMoneyFromAccountWithId5To6() {
        //given
        BigDecimal amount = BigDecimal.valueOf(123);

        Account sender = new Account("0104100100000002", BigDecimal.valueOf(10025.85), "EUR",
                4L, 4L, LocalDate.of(2023, 12, 4), true);
        sender.setId(5L);
        Account receiver = new Account("0123400100000001", BigDecimal.valueOf(0.00), "RUB",
                2L, 1L, LocalDate.of(2023, 12, 4), true);
        receiver.setId(7L);

        Account expectedSender = new Account("0001000100000001", BigDecimal.valueOf(9902.85), "EUR",
                4L, 4L, LocalDate.of(2023, 12, 4), true);
        expectedSender.setId(5L);
        Account expectedReceiver = new Account("0104100100000004", BigDecimal.valueOf(12577.98), "RUB",
                2L, 1L, LocalDate.of(2023, 12, 4), true);
        expectedReceiver.setId(7L);

        Transaction expectedTransaction = new Transaction(amount, "TRANSFER", sender.getCurrency(),
                null, receiver.getId(), null);

        //when
        Transaction transaction = accountRepository.transfer(sender, receiver, amount);
        Transaction actualTransaction = new Transaction(transaction.getAmount(), transaction.getType(),
                transaction.getCurrency(), transaction.getSenderAccountId(),
                transaction.getReceiverAccountId(), null);

        Account actualSender = accountRepository.findById(5L).get();
        Account actualReceiver = accountRepository.findById(7L).get();

        //then
        Assertions.assertEquals(expectedTransaction, actualTransaction);
        Assertions.assertEquals(expectedSender, actualSender);
        Assertions.assertEquals(expectedReceiver, actualReceiver);
    }
}