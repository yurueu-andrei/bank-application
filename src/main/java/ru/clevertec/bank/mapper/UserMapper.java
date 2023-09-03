package ru.clevertec.bank.mapper;

import ru.clevertec.bank.dto.UserRequestDto;
import ru.clevertec.bank.dto.UserResponseDto;
import ru.clevertec.bank.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Mapper interface responsible for mapping between User entities and their corresponding DTOs.
 * It provides methods for converting User objects to UserResponseDto objects and vice versa.
 * Additionally, it supports mapping lists of User entities to lists of UserResponseDto objects.
 *
 * @author Andrei Yuryeu
 * @see User
 * @see UserResponseDto
 */
@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    UserResponseDto toDto(User user);

    User fromDto(UserRequestDto dto);

    List<UserResponseDto> toListOfDto(List<User> users);
}
