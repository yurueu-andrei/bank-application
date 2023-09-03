package ru.clevertec.bank.mapper;

import ru.clevertec.bank.dto.TransactionRequestDto;
import ru.clevertec.bank.dto.TransactionResponseDto;
import ru.clevertec.bank.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Mapper interface responsible for mapping between Transaction entities and their corresponding DTOs.
 * It provides methods for converting Transaction objects to TransactionResponseDto objects and vice versa.
 * Additionally, it supports mapping lists of Transaction entities to lists of TransactionResponseDto objects.
 *
 * @author Andrei Yuryeu
 * @see Transaction
 * @see TransactionResponseDto
 */
@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface TransactionMapper {

    TransactionResponseDto toDto(Transaction transaction);

    Transaction fromDto(TransactionRequestDto dto);

    List<TransactionResponseDto> toListOfDto(List<Transaction> transactions);
}
