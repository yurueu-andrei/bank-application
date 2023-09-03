package ru.clevertec.bank.repository;

import ru.clevertec.bank.config.ApplicationConfig;
import ru.clevertec.bank.exception.RepositoryException;
import ru.clevertec.bank.entity.Account;
import ru.clevertec.bank.entity.Transaction;
import ru.clevertec.bank.util.yaml.Parser;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository class for managing operations on account entities.
 *
 * @author Andrei Yuryeu
 * @see Repository
 * @see Account
 */
public class AccountRepository extends Repository<Account> {

    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM accounts WHERE active = TRUE AND id = ?";
    private static final String SELECT_BY_NUMBER_QUERY = "SELECT * FROM accounts WHERE active = TRUE AND number = ?";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM accounts WHERE active = TRUE LIMIT ? OFFSET ?";

    private static final String INSERT_QUERY = """
        INSERT INTO accounts (number, balance, currency, user_id, bank_id, created_date, active)
        VALUES (?, 0, ?::currency_enum, ?, ?, ?, ?)
    """;

    private static final String UPDATE_QUERY = """
        UPDATE accounts SET number = ?, balance = ?, currency = ?::currency_enum, user_id = ?,
            bank_id = ?, created_date = ?, active = ?
        WHERE id = ?
    """;

    private static final String SELECT_BY_NUMBER_FOR_UPDATE_QUERY = """
         SELECT * FROM accounts WHERE number = ? FOR UPDATE
    """;

    private static final String SELECT_ALL_FOR_UPDATE_QUERY = """
        SELECT * FROM accounts FOR UPDATE
    """;

    private static final String UPDATE_ACCOUNT_BALANCE_QUERY = "UPDATE accounts SET balance = ? WHERE id = ?";
    private static final String APPLY_PERCENTAGE_QUERY = "UPDATE accounts SET balance = balance * ? WHERE bank_id = 1";
    private static final String DELETE_QUERY = "UPDATE accounts SET active = FALSE WHERE bank_id = 1 AND id = ?";

    private final Parser yamlParser;

    public AccountRepository(DataSource dataSource) {
        super(dataSource);
        this.yamlParser = ApplicationConfig.getYamlParser();
    }

    @Override
    protected String getSelectByIdQuery() {
        return SELECT_BY_ID_QUERY;
    }

    @Override
    protected String getSelectAllQuery() {
        return SELECT_ALL_QUERY;
    }

    @Override
    protected String getInsertQuery() {
        return INSERT_QUERY;
    }

    @Override
    protected String getUpdateQuery() {
        return UPDATE_QUERY;
    }

    @Override
    protected String getDeleteQuery() {
        return DELETE_QUERY;
    }

    @Override
    protected Account construct(ResultSet resultSet) throws SQLException {
        Account account = new Account();
        account.setId(resultSet.getLong("id"));
        account.setNumber(resultSet.getString("number"));
        account.setBalance(resultSet.getBigDecimal("balance"));
        account.setCurrency(resultSet.getString("currency"));
        account.setBankId(resultSet.getLong("bank_id"));
        account.setUserId(resultSet.getLong("user_id"));
        account.setCreatedDate(resultSet.getDate("created_date").toLocalDate());
        account.setActive(resultSet.getBoolean("active"));
        return account;
    }

    @Override
    protected void settingPreparedStatement(PreparedStatement preparedStatement, Account element) throws SQLException {
        preparedStatement.setString(1, element.getNumber());
        preparedStatement.setString(2, element.getCurrency());
        preparedStatement.setLong(3, element.getUserId());
        preparedStatement.setLong(4, element.getBankId());
        preparedStatement.setDate(5, Date.valueOf(LocalDate.now()));
        preparedStatement.setBoolean(6, true);
    }

    /**
     * Applies percentage on account balances with the given interest rate.
     *
     */
    public void applyPercentage(BigDecimal interestRate) throws RepositoryException {
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement selectForUpdate = connection.prepareStatement(SELECT_ALL_FOR_UPDATE_QUERY);
             PreparedStatement updateBalance = connection.prepareStatement(APPLY_PERCENTAGE_QUERY)
        ) {
            try {
                connection.setAutoCommit(false);
                selectForUpdate.executeQuery();
                updateBalance.setBigDecimal(1, interestRate);
                updateBalance.executeUpdate();
                connection.commit();
            } catch (Exception ex) {
                connection.setAutoCommit(true);
                connection.rollback();
            }
        } catch (Exception ex) {
            throw new RepositoryException("Failed to accrual interest [" + ex.getMessage() + "]");
        }
    }

    /**
     * Retrieves an account entity by its number.
     *
     * @param number The number of the account to retrieve.
     * @return An Optional containing the retrieved account if found, or empty if not found.
     * @throws RepositoryException If there is an error during the repository operation.
     */
    public Optional<Account> findByNumber(String number) throws RepositoryException {
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_NUMBER_QUERY)
        ) {
            preparedStatement.setString(1, number);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(construct(resultSet));
                } else {
                    return Optional.empty();
                }
            }
        } catch (Exception ex) {
            throw new RepositoryException("The entity was not found[" + ex.getMessage() + "]");
        }
    }

    /**
     * Retrieves an account entity by its unique number with a lock for update.
     *
     * @param number The unique number of the account to retrieve.
     * @return An Optional containing the retrieved account if found, or empty if not found.
     * @throws RepositoryException If there is an error during the repository operation.
     */
    public Optional<Account> blockingFindByNumber(String number) throws RepositoryException {
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_NUMBER_FOR_UPDATE_QUERY)
        ) {
            preparedStatement.setString(1, number);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? Optional.of(construct(resultSet)) : Optional.empty();
            }
        } catch (Exception ex) {
            throw new RepositoryException("The entity was not found[" + ex.getMessage() + "]");
        }
    }

    /**
     * Withdraws a specified amount from an account and returns the associated transaction.
     *
     */
    public Transaction withdraw(Account account, BigDecimal amount) {
        BigDecimal balance = account.getBalance().subtract(amount);
        account.setBalance(balance);
        Transaction transaction = fillTransaction(account, amount, "WITHDRAW");
        executeQuery(account);
        return transaction;
    }

    private Transaction fillTransaction(Account account, BigDecimal amount, String transactionType) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(transactionType);
        transaction.setCurrency(account.getCurrency());
        transaction.setReceiverAccountId(account.getId());
        transaction.setCreatedDate(LocalDateTime.now());
        return transaction;
    }

    /**
     * Refills an account with a specified amount and returns the associated transaction.
     *
     */
    public Transaction deposit(Account account, BigDecimal amount) {
        BigDecimal balance = account.getBalance().add(amount);
        account.setBalance(balance);
        Transaction transaction = fillTransaction(account, amount, "REFILL");
        executeQuery(account);
        return transaction;
    }

    private void executeQuery(Account account) {
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement updateAccount = connection.prepareStatement(UPDATE_ACCOUNT_BALANCE_QUERY);
        ) {
            try {
                connection.setAutoCommit(false);
                updateBalance(account, updateAccount);
            } catch (Exception ex) {
                connection.setAutoCommit(true);
                connection.rollback();
            }
            connection.setAutoCommit(true);
        } catch (Exception ex) {
            throw new RepositoryException("Balance of " + account.getClass().getSimpleName() + "account was not updated [" + ex.getMessage() + "]");
        }
    }

    private void updateBalance(Account account, PreparedStatement updateAccount) throws SQLException {
        updateAccount.setBigDecimal(1, account.getBalance());
        updateAccount.setLong(2, account.getId());
        updateAccount.executeUpdate();
    }

    /**
     * Transfers a specified amount from a sender account to a receiver account.
     *
     */
    public Transaction transfer(Account sender, Account receiver, BigDecimal amount) {
        BigDecimal senderBalance;
        BigDecimal receiverBalance;
        if (!sender.getCurrency().equals(receiver.getCurrency())) {
            BigDecimal currencyRate = yamlParser.getYaml().getExchangeRates()
                    .get(sender.getCurrency() + receiver.getCurrency());
            senderBalance = sender.getBalance().subtract(amount);
            receiverBalance = receiver.getBalance().add(amount.multiply(currencyRate));
        } else {
            senderBalance = sender.getBalance().subtract(amount);
            receiverBalance = receiver.getBalance().add(amount);
        }
        sender.setBalance(senderBalance);
        receiver.setBalance(receiverBalance);

        Transaction transaction = fillTransaction(sender, amount, "TRANSFER");
        transaction.setReceiverAccountId(receiver.getId());
        executeQuery(sender, receiver);
        return transaction;
    }

    private void executeQuery(Account sender, Account receiver) {
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement updateSender = connection.prepareStatement(UPDATE_ACCOUNT_BALANCE_QUERY);
             PreparedStatement updateReceiver = connection.prepareStatement(UPDATE_ACCOUNT_BALANCE_QUERY);
        ) {
            try {
                connection.setAutoCommit(false);
                updateBalance(sender, updateSender);
                updateBalance(receiver, updateReceiver);
            } catch (Exception ex) {
                connection.setAutoCommit(true);
                connection.rollback();
            }
            connection.setAutoCommit(true);
        } catch (Exception ex) {
            throw new RepositoryException("Balance of accounts was not updated [" + ex.getMessage() + "]");
        }
    }
}
