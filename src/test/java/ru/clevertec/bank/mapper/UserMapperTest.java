package ru.clevertec.bank.mapper;

import ru.clevertec.bank.dto.UserRequestDto;
import ru.clevertec.bank.dto.UserResponseDto;
import ru.clevertec.bank.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toDtoTest_shouldMapUserToUserResponseDto() {
        //given
        User user = new User("Ivan", "Kozlov", LocalDate.of(1998, 8, 1), true);
        user.setId(3L);
        var expected = new UserResponseDto(user.getId(), user.getName(), user.getSurname(), user.getBirthdate());

        //when
        UserResponseDto actual = userMapper.toDto(user);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void fromDtoTest_shouldMapUserRequestDtoIntoUser() {
        //given
        UserRequestDto userRequestDto = new UserRequestDto("Ivan", "Kozlov", LocalDate.of(1998, 8, 1));
        var expected = new User(userRequestDto.name(), userRequestDto.surname(), userRequestDto.birthdate(), null);
        expected.setId(3L);

        //when
        User actual = userMapper.fromDto(userRequestDto);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void toListOfDtoTest_shouldMapListOfUsersIntoListOfUserResponseDto() {
        //given
        User user1 = new User("Ivan", "Kozlov", LocalDate.of(1998, 8, 1), true);
        user1.setId(3L);
        User user2 = new User("Alex", "Petrov", LocalDate.of(2004, 12, 4), true);
        user1.setId(4L);

        List<User> users = new ArrayList<>() {{
            add(user1);
            add(user2);
        }};

        List<UserResponseDto> expected = users.stream().map(u -> new UserResponseDto(u.getId(), u.getName(), u.getSurname(), u.getBirthdate())).toList();

        //when
        List<UserResponseDto> actual = userMapper.toListOfDto(users);

        //then
        Assertions.assertEquals(expected, actual);
    }
}
