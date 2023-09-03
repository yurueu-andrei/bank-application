package ru.clevertec.bank.repository;

import ru.clevertec.bank.exception.RepositoryException;
import ru.clevertec.bank.entity.Transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing operations on transaction entities.
 *
 * @author Andrei Yuryeu
 * @see Repository
 * @see Transaction
 */
public class TransactionRepository extends Repository<Transaction> {

    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM transactions WHERE id = ?";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM transactions LIMIT ? OFFSET ?";

    private static final String SELECT_ALL_BY_ACCOUNT_FOR_PERIOD_QUERY = """
        SELECT * FROM transactions t
            LEFT JOIN accounts a ON t.sender_account_id = a.id OR t.receiver_account_id = a.id
        WHERE a.number = ? AND (t.created_date BETWEEN ? AND ?)
    """;

    private static final String INSERT_QUERY = """
        INSERT INTO transactions (amount, type, currency, sender_account_id, receiver_account_id, created_date)
        VALUES (?, ?::transaction_type_enum, ?::currency_enum, ?, ?, ?)
    """;
    private static final String UPDATE_QUERY = """
        UPDATE transactions SET amount = ?, type = ?::transaction_type_enum, currency= ?::currency_enum,
            sender_account_id = ?, receiver_account_id = ?, created_date = ?
        WHERE id = ?
    """;
    private static final String DELETE_QUERY = "DELETE FROM transactions WHERE id = ?";

    public TransactionRepository(DataSource dataSource) {
        super(dataSource);
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
    protected Transaction construct(ResultSet resultSet) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(resultSet.getLong("id"));
        transaction.setAmount(resultSet.getBigDecimal("amount"));
        transaction.setType(resultSet.getString("type"));
        transaction.setCurrency(resultSet.getString("currency"));
        transaction.setSenderAccountId(resultSet.getLong("sender_account_id"));
        transaction.setReceiverAccountId(resultSet.getLong("receiver_account_id"));
        transaction.setCreatedDate(resultSet.getTimestamp("created_date").toLocalDateTime());
        return transaction;
    }

    @Override
    protected void settingPreparedStatement(PreparedStatement preparedStatement, Transaction element) throws SQLException {
        preparedStatement.setBigDecimal(1, element.getAmount());
        preparedStatement.setString(2, element.getType());
        preparedStatement.setString(3, element.getCurrency());
        preparedStatement.setObject(4, element.getSenderAccountId());
        preparedStatement.setObject(5, element.getReceiverAccountId());
        preparedStatement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
    }

    /**
     * Retrieves a list of transactions for a specific account within a given date range.
     *
     * @param number    The account number for which transactions are to be retrieved.
     * @param startDate The start date of the date range.
     * @param endDate   The end date of the date range.
     * @return A list of transactions within the specified date range.
     * @throws RepositoryException If there is an error during the repository operation.
     */
    public List<Transaction> findAllForPeriod(String number, LocalDate startDate, LocalDate endDate) {
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_BY_ACCOUNT_FOR_PERIOD_QUERY)
        ) {
            preparedStatement.setString(1, number);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(startDate.atStartOfDay()));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(endDate.atStartOfDay()));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Transaction> found = new ArrayList<>();
                while (resultSet.next()) {
                    found.add(construct(resultSet));
                }
                return found;
            }
        } catch (Exception ex) {
            throw new RepositoryException("The entities were not found[" + ex.getMessage() + "]");
        }
    }
}
