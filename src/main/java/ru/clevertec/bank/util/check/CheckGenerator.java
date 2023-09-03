package ru.clevertec.bank.util.check;

import ru.clevertec.bank.exception.CheckGenerationException;
import ru.clevertec.bank.entity.Account;
import ru.clevertec.bank.entity.Transaction;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CheckGenerator {

    private static Long checkCounter = 12345L;

    public static void generateCheck(
            Transaction transaction,
            Account sender,
            Account receiver
    ) {
        try {
            List<String> lines = new ArrayList<>(List.of(
                    "Banking check",
                    "Check No: \t\t" + checkCounter,
                    LocalDate.now() + "\t\t" + LocalTime.now().truncatedTo(ChronoUnit.MINUTES),
                    "Type: \t\t" + transaction.getType()
            ));
            if ("TRANSFER".equals(transaction.getType())) {
                lines.add("Sender: \t" + sender.getNumber());
                lines.add("Receiver: \t" + receiver.getNumber());
            } else if ("WITHDRAW".equals(transaction.getType())) {
                lines.add("Client: \t" + sender.getNumber());
            } else {
                lines.add("Client: \t" + receiver.getNumber());
            }
            lines.add("Sum: \t\t" + transaction.getAmount() + " " + transaction.getCurrency());
            String rootPath = System.getProperty("user.dir");
            Path filePath = Paths.get(rootPath, "checks", "Check No" + checkCounter + ".txt");
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new CheckGenerationException("Unable to generate the check for this operation");
        }
        ++checkCounter;
    }
}
