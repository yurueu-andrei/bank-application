package ru.clevertec.bank.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.postgresql.ds.PGSimpleDataSource;
import ru.clevertec.bank.repository.AccountRepository;
import ru.clevertec.bank.repository.BankRepository;
import ru.clevertec.bank.repository.TransactionRepository;
import ru.clevertec.bank.repository.UserRepository;
import ru.clevertec.bank.service.AccountService;
import ru.clevertec.bank.service.BankService;
import ru.clevertec.bank.service.TransactionService;
import ru.clevertec.bank.service.UserService;
import ru.clevertec.bank.service.impl.AccountServiceImpl;
import ru.clevertec.bank.service.impl.BankServiceImpl;
import ru.clevertec.bank.service.impl.TransactionServiceImpl;
import ru.clevertec.bank.service.impl.UserServiceImpl;
import ru.clevertec.bank.util.yaml.Parser;

import javax.sql.DataSource;

/**
 * Configuration class responsible for initializing various components of the application.
 * This class sets up the data source, repositories, services, and other necessary objects used throughout the application.
 * It also provides access to commonly used instances such as the YAML parser and Jackson ObjectMapper.
 * All components are initialized as static fields for easy access throughout the application.
 *
 * @author Andrei Yuryeu
 */
public class ApplicationConfig {

    private static final AccountRepository accountRepository;
    private static final BankRepository bankRepository;
    private static final TransactionRepository transactionRepository;
    private static final UserRepository userRepository;
    private static final AccountService accountService;
    private static final BankService bankService;
    private static final TransactionService transactionService;
    private static final UserService userService;
    private static final Parser yamlParser;
    private static final DataSource dataSource;
    private static final ObjectMapper objectMapper;

    static {
        yamlParser = new Parser();

        PGSimpleDataSource pgSimpleDataSource = new PGSimpleDataSource();
        pgSimpleDataSource.setURL(yamlParser.getYaml().getPostgres().getUrl());
        pgSimpleDataSource.setUser(yamlParser.getYaml().getPostgres().getUser());
        pgSimpleDataSource.setPassword(yamlParser.getYaml().getPostgres().getPassword());
        dataSource = pgSimpleDataSource;

        accountRepository = new AccountRepository(dataSource);
        bankRepository = new BankRepository(dataSource);
        transactionRepository = new TransactionRepository(dataSource);
        userRepository = new UserRepository(dataSource);

        accountService = new AccountServiceImpl(accountRepository, transactionRepository);
        bankService = new BankServiceImpl(bankRepository);
        transactionService = new TransactionServiceImpl(transactionRepository);
        userService = new UserServiceImpl(userRepository);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static AccountService getAccountService() {
        return accountService;
    }

    public static BankService getBankService() {
        return bankService;
    }

    public static TransactionService getTransactionService() {
        return transactionService;
    }

    public static UserService getUserService() {
        return userService;
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static Parser getYamlParser() {
        return yamlParser;
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
