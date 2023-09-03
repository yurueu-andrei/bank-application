package ru.clevertec.bank.service.impl;

import ru.clevertec.bank.dto.AccountRequestDto;
import ru.clevertec.bank.dto.AccountResponseDto;
import ru.clevertec.bank.exception.EntityNotFoundException;
import ru.clevertec.bank.entity.Account;
import ru.clevertec.bank.entity.Transaction;
import ru.clevertec.bank.repository.AccountRepository;
import ru.clevertec.bank.repository.TransactionRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @AfterAll
    static void deleteChecksFolder() throws IOException {
        Path pathToBeDeleted = Paths.get("checks");
        Files.walkFileTree(pathToBeDeleted,
                new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult postVisitDirectory(
                            Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(
                            Path file, BasicFileAttributes attrs)
                            throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }
                });
    }

    @Test
    void findAllTest_shouldReturnAccountsWrappedIntoResponseDto() {
        //given
        LocalDate now = LocalDate.now();
        Account account1 = new Account("12345", BigDecimal.ONE,
                "USD", 3L, 3L, now, true);
        account1.setId(3L);
        Account account2 = new Account("12345", BigDecimal.ONE,
                "USD", 6L, 2L, now, true);
        account2.setId(4L);

        List<Account> accounts = new ArrayList<>() {{
            add(account1);
            add(account2);
        }};

        List<AccountResponseDto> expected = accounts.stream()
                .map(ac -> new AccountResponseDto(ac.getId(), ac.getNumber(), ac.getBalance(),
                        ac.getCurrency(), ac.getUserId(), ac.getBankId(), ac.getCreatedDate()))
                .toList();

        //when
        when(accountRepository.findAll(2, 2)).thenReturn(accounts);
        List<AccountResponseDto> actual = accountService.findAll(2, 2);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void saveTest_shouldReturnAccountResponseDtoWithId5() {
        //given
        Long id = 5L;
        LocalDate now = LocalDate.now();
        Account accountWithoutId = new Account("12345", null,
                "USD", 1L, 1L, null, null);
        Account accountWithId = new Account("12345", BigDecimal.ONE,
                "USD", 1L, 1L, now, true);
        accountWithId.setId(id);

        AccountRequestDto requestDto = new AccountRequestDto("12345", "USD", 1L, 1L);
        AccountResponseDto expected = new AccountResponseDto(id, "12345", BigDecimal.ONE,
                "USD", 1L, 1L, now);

        //when
        when(accountRepository.save(accountWithoutId)).thenReturn(accountWithId);
        AccountResponseDto actual = accountService.save(requestDto);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Nested
    class FindById {

        @Test
        void findByIdTest_shouldReturnAccountResponseDtoWithId1() {
            //given
            Long id = 1L;
            String number = "12345";
            LocalDate now = LocalDate.now();
            AccountResponseDto expected = new AccountResponseDto(id, number, BigDecimal.ONE,
                    "USD", 1L, 1L, now);

            Account account = new Account(number, BigDecimal.ONE,
                    "USD", 1L, 1L, now, true);
            account.setId(id);

            //when
            when(accountRepository.findById(id)).thenReturn(Optional.of(account));
            AccountResponseDto actual = accountService.findById(id);

            //then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        void findByIdTest_shouldThrowEntityNotFoundExceptionForNonExistentAccount() {
            //given
            Long id = 100L;

            //when
            when(accountRepository.findById(id)).thenReturn(Optional.empty());

            //then
            Assertions.assertThrows(EntityNotFoundException.class, () -> accountService.findById(id));
        }
    }

    @Nested
    class FindByNumber {

        @Test
        void findByNumberTest_shouldReturnAccountResponseDtoWithId1() {
            //given
            Long id = 1L;
            String number = "12345";
            LocalDate now = LocalDate.now();
            AccountResponseDto expected = new AccountResponseDto(id, number, BigDecimal.ONE,
                    "USD", 1L, 1L, now);

            Account account = new Account(number, BigDecimal.ONE,
                    "USD", 1L, 1L, now, true);
            account.setId(id);

            //when
            when(accountRepository.findByNumber(number)).thenReturn(Optional.of(account));
            AccountResponseDto actual = accountService.findByNumber(number);

            //then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        void findByNumberTest_shouldThrowEntityNotFoundExceptionForWrongNumber() {
            //given
            String number = "12345";

            //when
            when(accountRepository.findByNumber(number)).thenReturn(Optional.empty());

            //then
            Assertions.assertThrows(EntityNotFoundException.class, () -> accountService.findByNumber(number));
        }
    }

    @Nested
    class Delete {

        @Test
        void deleteTest_shouldDeleteAccountWithId3() {
            //given
            Long id = 3L;
            String number = "12345";
            LocalDate now = LocalDate.now();
            Account account = new Account(number, BigDecimal.ONE,
                    "USD", 1L, 1L, now, true);
            account.setId(id);

            //when
            when(accountRepository.findById(id)).thenReturn(Optional.of(account));
            when(accountRepository.delete(id)).thenReturn(true);
            boolean actual = accountService.delete(id);

            //then
            Assertions.assertTrue(actual);
            verify(accountRepository, times(1)).delete(id);
        }

        @Test
        void deleteTest_shouldThrowExceptionIfAccountIsNotFound() {
            //given
            Long id = 3L;

            //when
            when(accountRepository.findById(id)).thenReturn(Optional.empty());

            //then
            Assertions.assertThrows(EntityNotFoundException.class, () -> accountService.delete(id));
        }
    }

    @Nested
    class Withdraw {

        @Test
        void withdrawTest_shouldWithdrawMoneyFromAccountAndSaveTransaction() {
            //given
            Long id = 3L;
            String number = "12345";
            BigDecimal amount = BigDecimal.valueOf(5);
            Long bankId = 1L;
            LocalDate now = LocalDate.now();

            Account account = new Account(number, BigDecimal.valueOf(50),
                    "USD", 1L, bankId, now, true);
            account.setId(id);

            LocalDateTime trTime = LocalDateTime.now();
            Transaction transaction = new Transaction(amount, "WITHDRAW", "USD",
                    null, id, trTime);

            //when
            when(accountRepository.blockingFindByNumber(number)).thenReturn(Optional.of(account));
            when(accountRepository.withdraw(account, amount)).thenReturn(transaction);
            boolean result = accountService.withdraw(number, amount);

            //then
            Assertions.assertTrue(result);
            Mockito.verify(transactionRepository, times(1)).save(transaction);
        }

        @Test
        void withdrawTest_shouldThrowEntityNotFoundExceptionForNonExistentAccount() {
            //given
            String number = "12345";
            BigDecimal amount = BigDecimal.valueOf(5);

            //when
            when(accountRepository.blockingFindByNumber(number)).thenReturn(Optional.empty());

            //then
            Assertions.assertThrows(EntityNotFoundException.class, () -> accountService.withdraw(number, amount));
        }

        @Test
        void withdrawTest_shouldThrowUnsupportedOperationExceptionForAccountWithBankIdNot1() {
            //given
            Long id = 3L;
            String number = "12345";
            BigDecimal amount = BigDecimal.valueOf(5);
            Long bankId = 2L;
            LocalDate now = LocalDate.now();

            Account account = new Account(number, BigDecimal.valueOf(50),
                    "USD", 1L, bankId, now, true);
            account.setId(id);

            //when
            when(accountRepository.blockingFindByNumber(number)).thenReturn(Optional.of(account));

            //then
            Assertions.assertThrows(UnsupportedOperationException.class, () -> accountService.withdraw(number, amount));
        }

        @Test
        void withdrawTest_shouldThrowUnsupportedOperationExceptionWhenAccountBalanceIsLessThenWithdrawAmount() {
            //given
            Long id = 3L;
            String number = "12345";
            BigDecimal amount = BigDecimal.valueOf(50);
            Long bankId = 1L;
            LocalDate now = LocalDate.now();
            Account account = new Account(number, BigDecimal.valueOf(5),
                    "USD", 1L, bankId, now, true);
            account.setId(id);

            //when
            when(accountRepository.blockingFindByNumber(number)).thenReturn(Optional.of(account));

            //then
            Assertions.assertThrows(UnsupportedOperationException.class, () -> accountService.withdraw(number, amount));
        }
    }

    @Nested
    class Deposit {

        @Test
        void depositTest_shouldRefillMoneyOfAccountAndSaveTransaction() {
            //given
            Long id = 3L;
            String number = "12345";
            BigDecimal amount = BigDecimal.valueOf(5);
            Long bankId = 1L;
            LocalDate now = LocalDate.now();
            Account account = new Account(number, BigDecimal.valueOf(50),
                    "USD", 1L, bankId, now, true);
            account.setId(id);

            LocalDateTime trTime = LocalDateTime.now();
            Transaction transaction = new Transaction(amount, "REFILL", "USD",
                    null, id, trTime);

            //when
            when(accountRepository.blockingFindByNumber(number)).thenReturn(Optional.of(account));
            when(accountRepository.deposit(account, amount)).thenReturn(transaction);
            boolean result = accountService.deposit(number, amount);

            //then
            Assertions.assertTrue(result);
            Mockito.verify(transactionRepository, times(1)).save(transaction);
        }

        @Test
        void depositTest_shouldThrowEntityNotFoundExceptionForNonExistentAccount() {
            //given
            String number = "12345";
            BigDecimal amount = BigDecimal.valueOf(5);

            //when
            when(accountRepository.blockingFindByNumber(number)).thenReturn(Optional.empty());

            //then
            Assertions.assertThrows(EntityNotFoundException.class, () -> accountService.deposit(number, amount));
        }

        @Test
        void depositTest_shouldThrowUnsupportedOperationExceptionForAccountWithBankIdNot1() {
            //given
            Long id = 3L;
            String number = "12345";
            BigDecimal amount = BigDecimal.valueOf(5);
            Long bankId = 2L;
            LocalDate now = LocalDate.now();
            Account account = new Account(number, BigDecimal.valueOf(50),
                    "USD", 1L, bankId, now, true);
            account.setId(id);

            //when
            when(accountRepository.blockingFindByNumber(number)).thenReturn(Optional.of(account));

            //then
            Assertions.assertThrows(UnsupportedOperationException.class, () -> accountService.withdraw(number, amount));
        }
    }

    @Nested
    class Transfer {

        @Test
        void transferTest_shouldTransferMoneyWhenBothAccountsExistAndFromCleverbankAndSenderBalanceIsGreaterThanTransferAmount() {
            //given
            Long senderId = 3L;
            Long receiverId = 4L;
            String senderNumber = "12345";
            String receiverNumber = "67890";
            BigDecimal amount = BigDecimal.valueOf(15);
            LocalDate now = LocalDate.now();

            Account sender = new Account(senderNumber, BigDecimal.valueOf(50),
                    "USD", 1L, 1L, now, true);
            sender.setId(senderId);
            Account receiver = new Account(receiverNumber, BigDecimal.valueOf(50),
                    "USD", 2L, 1L, now, true);
            receiver.setId(receiverId);

            LocalDateTime trTime = LocalDateTime.now();
            Transaction transaction = new Transaction(amount, "TRANSFER", "USD",
                    senderId, receiverId, trTime);

            //when
            when(accountRepository.blockingFindByNumber(senderNumber)).thenReturn(Optional.of(sender));
            when(accountRepository.blockingFindByNumber(receiverNumber)).thenReturn(Optional.of(receiver));
            when(accountRepository.transfer(sender, receiver, amount)).thenReturn(transaction);
            boolean result = accountService.transfer(senderNumber, receiverNumber, amount);

            //then
            Assertions.assertTrue(result);
            Mockito.verify(transactionRepository, times(1)).save(transaction);
        }

        @Test
        void transferTest_shouldTransferMoneyWhenBothAccountExistAndOnlySenderIsCleverbankAndSenderBalanceIsGreaterThanTransferAmount() {
            //given
            Long senderId = 3L;
            Long receiverId = 4L;
            String senderNumber = "12345";
            String receiverNumber = "67890";
            BigDecimal amount = BigDecimal.valueOf(15);
            LocalDate now = LocalDate.now();

            Account sender = new Account(senderNumber, BigDecimal.valueOf(50),
                    "USD", 1L, 1L, now, true);
            sender.setId(senderId);
            Account receiver = new Account(receiverNumber, BigDecimal.valueOf(50),
                    "USD", 2L, 2L, now, true);
            receiver.setId(receiverId);

            LocalDateTime trTime = LocalDateTime.now();
            Transaction transaction = new Transaction(amount, "TRANSFER", "USD",
                    senderId, receiverId, trTime);

            //when
            when(accountRepository.blockingFindByNumber(senderNumber)).thenReturn(Optional.of(sender));
            when(accountRepository.blockingFindByNumber(receiverNumber)).thenReturn(Optional.of(receiver));
            when(accountRepository.transfer(sender, receiver, amount)).thenReturn(transaction);
            boolean result = accountService.transfer(senderNumber, receiverNumber, amount);

            //then
            Assertions.assertTrue(result);
            Mockito.verify(transactionRepository, times(1)).save(transaction);
        }

        @Test
        void transferTest_shouldTransferMoneyWhenBothAccountExistAndOnlyReceiverIsCleverbankAndSenderBalanceIsGreaterThanTransferAmount() {
            //given
            Long senderId = 3L;
            Long receiverId = 4L;
            String senderNumber = "12345";
            String receiverNumber = "67890";
            BigDecimal amount = BigDecimal.valueOf(15);
            LocalDate now = LocalDate.now();

            Account sender = new Account(senderNumber, BigDecimal.valueOf(50),
                    "USD", 1L, 2L, now, true);
            sender.setId(senderId);
            Account receiver = new Account(receiverNumber, BigDecimal.valueOf(50),
                    "USD", 2L, 1L, now, true);
            receiver.setId(receiverId);

            LocalDateTime trTime = LocalDateTime.now();
            Transaction transaction = new Transaction(amount, "TRANSFER", "USD",
                    senderId, receiverId, trTime);

            //when
            when(accountRepository.blockingFindByNumber(senderNumber)).thenReturn(Optional.of(sender));
            when(accountRepository.blockingFindByNumber(receiverNumber)).thenReturn(Optional.of(receiver));
            when(accountRepository.transfer(sender, receiver, amount)).thenReturn(transaction);
            boolean result = accountService.transfer(senderNumber, receiverNumber, amount);

            //then
            Assertions.assertTrue(result);
            Mockito.verify(transactionRepository, times(1)).save(transaction);
        }

        @Test
        void transferTest_shouldThrowExceptionIfBothAccountAreNotFromCleverbank() {
            //given
            Long senderId = 3L;
            Long receiverId = 4L;
            String senderNumber = "12345";
            String receiverNumber = "67890";
            BigDecimal amount = BigDecimal.valueOf(15);
            LocalDate now = LocalDate.now();

            Account sender = new Account(senderNumber, BigDecimal.valueOf(50),
                    "USD", 1L, 2L, now, true);
            sender.setId(senderId);
            Account receiver = new Account(receiverNumber, BigDecimal.valueOf(50),
                    "USD", 2L, 2L, now, true);
            receiver.setId(receiverId);

            //when
            when(accountRepository.blockingFindByNumber(senderNumber)).thenReturn(Optional.of(sender));
            when(accountRepository.blockingFindByNumber(receiverNumber)).thenReturn(Optional.of(receiver));

            //then
            Assertions.assertThrows(UnsupportedOperationException.class, () ->
                    accountService.transfer(senderNumber, receiverNumber, amount));
        }

        @Test
        void transferTest_shouldThrowExceptionIfTransferAmountIsGreaterThanSenderAccountBalance() {
            //given
            Long senderId = 3L;
            Long receiverId = 4L;
            String senderNumber = "12345";
            String receiverNumber = "67890";
            BigDecimal amount = BigDecimal.valueOf(150);
            LocalDate now = LocalDate.now();

            Account sender = new Account(senderNumber, BigDecimal.valueOf(50),
                    "USD", 1L, 2L, now, true);
            sender.setId(senderId);
            Account receiver = new Account(receiverNumber, BigDecimal.valueOf(50),
                    "USD", 2L, 2L, now, true);
            receiver.setId(receiverId);

            //when
            when(accountRepository.blockingFindByNumber(senderNumber)).thenReturn(Optional.of(sender));
            when(accountRepository.blockingFindByNumber(receiverNumber)).thenReturn(Optional.of(receiver));

            //then
            Assertions.assertThrows(UnsupportedOperationException.class, () ->
                    accountService.transfer(senderNumber, receiverNumber, amount));
        }

        @Test
        void transferTest_shouldThrowEntityNotFoundExceptionIfSenderAccountDoesNotExist() {
            //given
            String senderNumber = "12345";
            String receiverNumber = "67890";
            BigDecimal amount = BigDecimal.valueOf(150);

            //when
            when(accountRepository.blockingFindByNumber(senderNumber)).thenReturn(Optional.empty());

            //then
            Assertions.assertThrows(EntityNotFoundException.class, () ->
                    accountService.transfer(senderNumber, receiverNumber, amount));
        }

        @Test
        void transferTest_shouldThrowEntityNotFoundExceptionIfReceiverAccountDoesNotExist() {
            //given
            Long senderId = 3L;
            String senderNumber = "12345";
            String receiverNumber = "67890";
            BigDecimal amount = BigDecimal.valueOf(150);
            LocalDate now = LocalDate.now();
            Account sender = new Account(senderNumber, BigDecimal.valueOf(50),
                    "USD", 1L, 2L, now, true);
            sender.setId(senderId);

            //when
            when(accountRepository.blockingFindByNumber(senderNumber)).thenReturn(Optional.of(sender));
            when(accountRepository.blockingFindByNumber(receiverNumber)).thenReturn(Optional.empty());

            //then
            Assertions.assertThrows(EntityNotFoundException.class, () ->
                    accountService.transfer(senderNumber, receiverNumber, amount));
        }
    }
}