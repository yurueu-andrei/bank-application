package ru.clevertec.bank.mapper;

import ru.clevertec.bank.dto.TransactionRequestDto;
import ru.clevertec.bank.dto.TransactionResponseDto;
import ru.clevertec.bank.entity.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class TransactionMapperTest {

    private final TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);

    @Test
    void toDtoTest_shouldMapTransactionToTransactionResponseDto() {
        //given
        Transaction transaction = new Transaction(
                BigDecimal.valueOf(100.11), "WITHDRAW", "BYN", 0L, 2L,
                LocalDateTime.of(2023, 12, 18, 12, 11, 7, 0));
        transaction.setId(1L);
        var expected = new TransactionResponseDto(
                transaction.getId(), transaction.getAmount(), transaction.getType(), transaction.getCurrency(),
                transaction.getSenderAccountId(), transaction.getReceiverAccountId(), transaction.getCreatedDate());

        //when
        TransactionResponseDto actual = transactionMapper.toDto(transaction);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void fromDtoTest_shouldMapTransactionRequestDtoIntoTransaction() {
        //given
        TransactionRequestDto transactionRequestDto = new TransactionRequestDto(
                BigDecimal.valueOf(100.11), "WITHDRAW", "BYN", 0L, 2L
        );
        var expected = new Transaction(
                transactionRequestDto.amount(), transactionRequestDto.type(), transactionRequestDto.currency(),
                transactionRequestDto.senderAccountId(), transactionRequestDto.receiverAccountId(), null);
        expected.setId(3L);

        //when
        Transaction actual = transactionMapper.fromDto(transactionRequestDto);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void toListOfDtoTest_shouldMapListOfTransactionsIntoListOfTransactionResponseDto() {
        //given
        Transaction transaction1 = new Transaction(
                BigDecimal.valueOf(100.11), "WITHDRAW", "BYN", 0L, 2L,
                LocalDateTime.of(2023, 12, 18, 12, 11, 7, 0));
        transaction1.setId(1L);
        Transaction transaction2 = new Transaction(
                BigDecimal.valueOf(123.22), "WITHDRAW", "RUB", 0L, 1L,
                LocalDateTime.of(2023, 9, 22, 10, 11, 7, 0));
        transaction2.setId(2L);

        List<Transaction> transactions = new ArrayList<>() {{
            add(transaction1);
            add(transaction2);
        }};

        List<TransactionResponseDto> expected = transactions.stream().map(t -> new TransactionResponseDto(t.getId(), t.getAmount(), t.getType(), t.getCurrency(),
                t.getSenderAccountId(), t.getReceiverAccountId(), t.getCreatedDate())).toList();

        //when
        List<TransactionResponseDto> actual = transactionMapper.toListOfDto(transactions);

        //then
        Assertions.assertEquals(expected, actual);
    }
}
