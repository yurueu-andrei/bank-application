package ru.clevertec.bank.util.statement;

import lombok.experimental.UtilityClass;
import ru.clevertec.bank.dto.AccountResponseDto;
import ru.clevertec.bank.dto.TransactionResponseDto;
import ru.clevertec.bank.dto.UserResponseDto;
import ru.clevertec.bank.exception.CheckGenerationException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class PdfStatementGenerator {

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static Long statementCounter = 1L;

    public static boolean generatePdfMoneyStatement(
            UserResponseDto user,
            AccountResponseDto account,
            BigDecimal income,
            BigDecimal outcome,
            LocalDate from,
            LocalDate to
    ) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(250, page.getMediaBox().getHeight() - 100);
                List<String> lines = new ArrayList<>(List.of(
                        "Client: " + user.name() + " " + user.surname(),
                        "Account: " + account.number(),
                        "Currency: " + account.currency(),
                        "Account create date: " + account.createdDate().format(formatter),
                        "Period: " + from.format(formatter) + "-" + to.format(formatter),
                        "Created at: " + LocalDate.now().format(formatter) + " " + LocalTime.now().truncatedTo(ChronoUnit.MINUTES)
                ));
                contentStream.showText("Money statement");
                contentStream.newLineAtOffset(25, -15);
                contentStream.showText("CleverBank");
                contentStream.newLineAtOffset(-55, -15);
                for (String line : lines) {
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -15);
                }
                contentStream.newLineAtOffset(-35, -15);
                contentStream.setWordSpacing(50);
                contentStream.showText("Income   Outcome");
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText(
                        income.setScale(2, RoundingMode.CEILING) + account.currency() + "  "
                                + outcome.setScale(2, RoundingMode.CEILING) + account.currency()
                );
                contentStream.endText();
            }
            String rootPath = System.getProperty("user.dir");
            Path filePath = Paths.get(rootPath, "statements", "money", "Statement" + statementCounter + ".pdf");
            Files.createDirectories(filePath.getParent());
            document.save(filePath.toFile());
        } catch (IOException e) {
            throw new CheckGenerationException("Unable to generate the PDF statement for this account");
        }
        return true;
    }

    public static boolean generatePdfAccountStatement(
            UserResponseDto user,
            List<TransactionResponseDto> transactions,
            AccountResponseDto account,
            LocalDate from,
            LocalDate to
    ) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(250, page.getMediaBox().getHeight() - 100);
                List<String> lines = new ArrayList<>(List.of(
                        "Client: " + user.name() + " " + user.surname(),
                        "Account: " + account.number(),
                        "Currency: " + account.currency(),
                        "Account create date: " + account.createdDate().format(formatter),
                        "Period: " + from.format(formatter) + "-" + to.format(formatter),
                        "Created at: " + LocalDate.now().format(formatter) + " " + LocalTime.now().truncatedTo(ChronoUnit.MINUTES)
                ));
                contentStream.showText("Account statement");
                contentStream.newLineAtOffset(25, -15);
                contentStream.showText("CleverBank");
                contentStream.newLineAtOffset(-55, -15);
                for (String line : lines) {
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -15);
                }
                contentStream.newLineAtOffset(-35, -15);
                contentStream.setWordSpacing(80);
                contentStream.showText("Date Note Sum");
                contentStream.newLineAtOffset(0, -15);
                contentStream.setWordSpacing(32);
                for (TransactionResponseDto transaction : transactions) {
                    if ("REFILL".equals(transaction.type())) {
                        contentStream.showText(transaction.createdDate().format(formatter) + " " + transaction.type() + "  " + transaction.amount());
                        contentStream.newLineAtOffset(0, -15);
                    } else {
                        contentStream.showText(transaction.createdDate().format(formatter) + " " + transaction.type() + " " + transaction.amount());
                        contentStream.newLineAtOffset(0, -15);
                    }
                }

                contentStream.endText();
            }
            String rootPath = System.getProperty("user.dir");
            Path filePath = Paths.get(rootPath, "statements", "accounts", "Statement" + statementCounter + ".pdf");
            Files.createDirectories(filePath.getParent());
            document.save(filePath.toFile());
            ++statementCounter;
        } catch (IOException e) {
            throw new CheckGenerationException("Unable to generate the PDF statement for this account");
        }
        return true;
    }
}