package ru.clevertec.bank.repository;

import ru.clevertec.bank.exception.RepositoryException;
import ru.clevertec.bank.entity.Bank;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class BankRepositoryTest extends BaseRepositoryTest {

    private final BankRepository bankRepository = new BankRepository(dataSource);

    @Test
    void findAllTest_shouldReturnBanksWithId1And2() {
        //given
        Bank bank1 = new Bank("CleverBank", true);
        bank1.setId(1L);
        Bank bank2 = new Bank("OtherBank1", true);
        bank2.setId(2L);

        List<Bank> expected = new ArrayList<>() {{
            add(bank1);
            add(bank2);
        }};

        //when
        List<Bank> actual = bankRepository.findAll(2, 0);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void updateTest_shouldUpdateBankWithId2AndReturnTrue() {
        //given
        Long id = 2L;
        Bank expected = new Bank("OtherBank123", true);
        expected.setId(id);

        //when
        boolean result = bankRepository.update(expected);
        Optional<Bank> foundUser = bankRepository.findById(expected.getId());

        //then
        Assertions.assertTrue(result);
        Assertions.assertEquals(expected, foundUser.get());
    }

    @Test
    void deleteTest_shouldDeleteBankWithId4AndReturnTrue() {
        //given
        Long id = 4L;

        //when
        boolean result = bankRepository.delete(id);
        Optional<Bank> foundUser = bankRepository.findById(id);

        //then
        Assertions.assertTrue(result);
        Assertions.assertFalse(foundUser.isPresent());
    }

    @Nested
    class FindById {

        @Test
        void findByIdTest_shouldReturnBankWithId1() {
            //given
            Long id = 1L;
            Bank bank = new Bank("CleverBank", true);
            bank.setId(id);

            Optional<Bank> expected = Optional.of(bank);

            //when
            Optional<Bank> actual = bankRepository.findById(id);

            //then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        void findByIdTest_shouldReturnEmptyOptional() {
            //given
            Long id = 100L;
            Optional<Bank> expected = Optional.empty();

            //when
            Optional<Bank> actual = bankRepository.findById(id);

            //then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class FindByAccountId {

        @Test
        void findByAccountIdTest_shouldReturnBankWithAccount2() {
            //given
            Long accountId = 2L;
            Bank user = new Bank("CleverBank", true);
            user.setId(3L);
            Optional<Bank> expected = Optional.of(user);

            //when
            Optional<Bank> actual = bankRepository.findByAccountId(accountId);

            //then
            Assertions.assertEquals(expected, actual);
        }

        @Test
        void findByAccountIdTest_shouldReturnEmptyOptional() {
            //given
            Long accountId = 200L;
            Optional<Bank> expected = Optional.empty();

            //when
            Optional<Bank> actual = bankRepository.findByAccountId(accountId);

            //then
            Assertions.assertEquals(expected, actual);
        }
    }

    @Nested
    class Save {

        @Test
        void saveTest_shouldReturnBankWithId5AndAddBankToDB() {
            //given
            Bank bankWithoutId = new Bank("OtherBank4", true);
            Bank expected = new Bank("OtherBank4", true);
            expected.setId(5L);

            //when
            Bank actual = bankRepository.save(bankWithoutId);
            Optional<Bank> actualInDB = bankRepository.findById(5L);

            //then
            Assertions.assertEquals(expected, actual);
            Assertions.assertEquals(expected, actualInDB.get());
        }

        @Test
        void saveTest_shouldThrowExceptionIfAnyFieldIsNull() {
            //given
            Bank bankWithoutId = new Bank(null, true);

            //then
            Assertions.assertThrows(RepositoryException.class, () -> bankRepository.save(bankWithoutId));
        }
    }
}