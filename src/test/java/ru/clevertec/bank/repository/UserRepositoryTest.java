package ru.clevertec.bank.repository;

import ru.clevertec.bank.exception.RepositoryException;
import ru.clevertec.bank.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class UserRepositoryTest extends BaseRepositoryTest {

    private final UserRepository userRepository = new UserRepository(dataSource);

    @Test
    void findAllTest_shouldReturnUsersWithId3And4() {
        //given
        User user1 = new User("Ivan", "Kozlov",
                LocalDate.of(1998, 8, 1), true);
        user1.setId(3L);
        User user2 = new User("Alex", "Petrov",
                LocalDate.of(2004, 12, 4), true);
        user1.setId(4L);

        List<User> expected = new ArrayList<>() {{
            add(user1);
            add(user2);
        }};

        //when
        List<User> actual = userRepository.findAll(2, 2);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void updateTest_shouldUpdateUserWithId2AndReturnTrue() {
        //given
        Long id = 2L;
        User expected = new User("Vlad", "Kolyago",
                LocalDate.of(2000, 1, 1), true);
        expected.setId(id);

        //when
        boolean result = userRepository.update(expected);
        Optional<User> foundUser = userRepository.findById(expected.getId());

        //then
        Assertions.assertTrue(result);
        Assertions.assertEquals(expected, foundUser.get());
    }

    @Test
    void deleteTest_shouldDeleteUserWithId4AndReturnTrue() {
        //given
        Long id = 4L;

        //when
        boolean result = userRepository.delete(id);
        Optional<User> foundUser = userRepository.findById(id);

        //then
        Assertions.assertTrue(result);
        Assertions.assertFalse(foundUser.isPresent());
    }

    @Nested
    class FindById {

        @Test
        void findByIdTest_shouldReturnUserWithId1() {
            //given
            Long id = 1L;
            User user = new User("Maria", "Ivanova",
                    LocalDate.of(2001, 11, 3), true);
            user.setId(id);

            Optional<User> expected = Optional.of(user);

            //when
            Optional<User> actual = userRepository.findById(id);

            //then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        void findByIdTest_shouldReturnEmptyOptional() {
            //given
            Long id = 100L;
            Optional<User> expected = Optional.empty();

            //when
            Optional<User> actual = userRepository.findById(id);

            //then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class FindByAccountId {

        @Test
        void findByAccountIdTest_shouldReturnUserWithAccount2() {
            //given
            Long accountId = 2L;
            User user = new User("Ivan", "Kozlov",
                    LocalDate.of(1998, 8, 1), true);
            user.setId(3L);
            Optional<User> expected = Optional.of(user);

            //when
            Optional<User> actual = userRepository.findByAccountId(accountId);

            //then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        void findByAccountIdTest_shouldReturnEmptyOptional() {
            //given
            Long accountId = 200L;
            Optional<User> expected = Optional.empty();

            //when
            Optional<User> actual = userRepository.findByAccountId(accountId);

            //then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class Save {

        @Test
        void saveTest_shouldReturnUserWithId5AndAddUserToDB() {
            //given
            User userWithoutId = new User("Nastya", "Yurkova",
                    LocalDate.of(2003, 1, 10), true);
            User expected = new User("Nastya", "Yurkova",
                    LocalDate.of(2003, 1, 10), true);
            expected.setId(5L);

            //when
            User actual = userRepository.save(userWithoutId);
            Optional<User> actualInDB = userRepository.findById(5L);

            //then
            Assertions.assertEquals(expected, actual);
            Assertions.assertEquals(expected, actualInDB.get());
        }

        @Test
        void saveTest_shouldThrowExceptionIfAnyFieldIsNull() {
            //given
            User userWithoutId = new User(null, "Yurkova",
                    LocalDate.of(2003, 1, 10), true);

            //then
            Assertions.assertThrows(RepositoryException.class, () -> userRepository.save(userWithoutId));
        }
    }
}