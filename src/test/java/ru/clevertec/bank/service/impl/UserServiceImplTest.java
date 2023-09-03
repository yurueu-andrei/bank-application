package ru.clevertec.bank.service.impl;

import ru.clevertec.bank.dto.UserRequestDto;
import ru.clevertec.bank.dto.UserResponseDto;
import ru.clevertec.bank.exception.EntityNotFoundException;
import ru.clevertec.bank.entity.User;
import ru.clevertec.bank.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findAllTest_shouldReturnUsersWrappedIntoResponseDto() {
        //given
        User user1 = new User("Ivan", "Kozlov", LocalDate.of(1998, 8, 1), true);
        user1.setId(3L);
        User user2 = new User("Alex", "Petrov", LocalDate.of(2004, 12, 4), true);

        List<User> users = new ArrayList<>() {{
            add(user1);
            add(user2);
        }};
        List<UserResponseDto> expected = users.stream()
                .map(u -> new UserResponseDto(u.getId(), u.getName(), u.getSurname(), u.getBirthdate()))
                .toList();

        //when
        when(userRepository.findAll(2, 2)).thenReturn(users);
        List<UserResponseDto> actual = userService.findAll(2, 2);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void saveTest_shouldReturnUserResponseDtoWithId5() {
        //given
        LocalDate now = LocalDate.now();
        User userWithoutId = new User("Nastya", "Yurkova", now, null);
        User userWithId = new User("Nastya", "Yurkova", now, true);
        userWithId.setId(5L);

        UserRequestDto requestDto = new UserRequestDto("Nastya", "Yurkova", now);
        UserResponseDto expected = new UserResponseDto(5L, "Nastya", "Yurkova", now);

        //when
        when(userRepository.save(userWithoutId)).thenReturn(userWithId);
        UserResponseDto actual = userService.save(requestDto);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Nested
    class FindById {

        @Test
        void findByIdTest_shouldReturnUserResponseDtoWithId1() {
            //given
            Long id = 1L;
            UserResponseDto expected = new UserResponseDto(1L,
                    "Maria", "Ivanova",
                    LocalDate.of(2001, 11, 3));
            User user = new User("Maria", "Ivanova",
                    LocalDate.of(2001, 11, 3), true);
            user.setId(1L);

            //when
            when(userRepository.findById(id)).thenReturn(Optional.of(user));
            UserResponseDto actual = userService.findById(id);

            //then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        void findByIdTest_shouldThrowEntityNotFoundExceptionForNonExistentUser() {
            //given
            Long id = 100L;

            //when
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            //then
            Assertions.assertThrows(EntityNotFoundException.class, () -> userService.findById(id));
        }
    }

    @Nested
    class FindByAccountId {

        @Test
        void findByAccountIdTest_shouldReturnUserResponseDtoWithId1() {
            //given
            Long id = 1L;
            UserResponseDto expected = new UserResponseDto(1L,
                    "Maria", "Ivanova",
                    LocalDate.of(2001, 11, 3));
            User user = new User("Maria", "Ivanova",
                    LocalDate.of(2001, 11, 3), true);
            user.setId(id);

            //when
            when(userRepository.findByAccountId(id)).thenReturn(Optional.of(user));
            UserResponseDto actual = userService.findByAccountId(id);

            //then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        void findByAccountIdTest_shouldThrowEntityNotFoundExceptionForWrongAccountId() {
            //given
            Long id = 100L;

            //when
            when(userRepository.findByAccountId(id)).thenReturn(Optional.empty());

            //then
            Assertions.assertThrows(EntityNotFoundException.class, () -> userService.findByAccountId(id));
        }
    }

    @Nested
    class Update {

        @Test
        void updateTest_shouldReturnTrueInCaseOfSuccessfulUpdate() {
            //given
            Long id = 3L;
            LocalDate now = LocalDate.now();
            UserRequestDto requestDto = new UserRequestDto("Nastya", "Yurkova", now);
            User userWithId = new User("Nastya", "Yurkova", now, true);
            userWithId.setId(id);
            User user = new User("Nastya", "Yurkova", now, null);

            //when
            when(userRepository.findById(id)).thenReturn(Optional.of(userWithId));
            when(userRepository.update(user)).thenReturn(true);
            boolean actual = userService.update(id, requestDto);

            //then
            Assertions.assertTrue(actual);
        }

        @Test
        void updateTest_shouldThrowExceptionIfUserIsNotFound() {
            //given
            Long id = 3L;
            LocalDate now = LocalDate.now();
            UserRequestDto requestDto = new UserRequestDto("Nastya", "Yurkova", now);

            //when
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            //then
            Assertions.assertThrows(EntityNotFoundException.class, () -> userService.update(id, requestDto));
        }
    }

    @Nested
    class Delete {

        @Test
        void deleteTest_shouldDeleteUserWithId3() {
            //given
            Long id = 3L;
            LocalDate now = LocalDate.now();
            User userWithId = new User("Nastya", "Yurkova", now, true);
            userWithId.setId(id);

            //when
            when(userRepository.findById(id)).thenReturn(Optional.of(userWithId));
            when(userRepository.delete(id)).thenReturn(true);
            boolean actual = userService.delete(id);

            //then
            Assertions.assertTrue(actual);
            verify(userRepository, times(1)).delete(id);
        }

        @Test
        void deleteTest_shouldThrowExceptionIfUserIsNotFound() {
            //given
            Long id = 3L;

            //when
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            //then
            Assertions.assertThrows(EntityNotFoundException.class, () -> userService.delete(id));
        }
    }
}