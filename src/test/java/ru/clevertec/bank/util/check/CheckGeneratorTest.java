package ru.clevertec.bank.util.check;

import ru.clevertec.bank.entity.Account;
import ru.clevertec.bank.entity.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
import java.util.List;

public class CheckGeneratorTest {

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
    void generateCheckTest_shouldSaveTxtFileWithTransactionInfo() throws IOException {
        //given
        Transaction transaction = new Transaction(
                BigDecimal.valueOf(100.11), "TRANSFER", "BYN", 1L, 2L,
                LocalDateTime.of(2023, 12, 18, 12, 11, 7, 0));

        Account sender = new Account("0104100100000001", BigDecimal.valueOf(1234.31), "BYN",
                1L, 1L, LocalDate.of(2001, 11, 18), true);
        sender.setId(1L);
        Account receiver = new Account("0104100100000002", BigDecimal.valueOf(500123.01), "RUB",
                3L, 1L, LocalDate.of(2022, 9, 22), true);
        receiver.setId(2L);

        //when
        CheckGenerator.generateCheck(transaction, sender, receiver);
        List<String> checkLines = Files.readAllLines(Path.of("checks/Check No12345.txt"));

        //then

        Assertions.assertAll(
                () -> Assertions.assertTrue(checkLines.get(3).contains("TRANSFER")),
                () -> Assertions.assertTrue(checkLines.get(4).contains(sender.getNumber())),
                () -> Assertions.assertTrue(checkLines.get(5).contains(receiver.getNumber())),
                () -> Assertions.assertTrue(checkLines.get(6).contains(transaction.getAmount().toString()))
        );
    }
}
