package ru.clevertec.bank.service;

import ru.clevertec.bank.dto.UserRequestDto;
import ru.clevertec.bank.dto.UserResponseDto;

import java.util.List;

/**
 * Service interface for managing users.
 *
 * @author Andrei Yuryeu
 */
public interface UserService {

    UserResponseDto findById(Long id);

    UserResponseDto findByAccountId(Long accountId);

    List<UserResponseDto> findAll(int limit, int offset);

    UserResponseDto save(UserRequestDto user);

    boolean update(Long id, UserRequestDto userDto);

    boolean delete(Long id);
}
