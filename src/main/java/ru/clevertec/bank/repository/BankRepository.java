package ru.clevertec.bank.repository;

import ru.clevertec.bank.exception.RepositoryException;
import ru.clevertec.bank.entity.Bank;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Repository class for managing operations on bank entities.
 *
 * @author Andrei Yuryeu
 * @see Repository
 * @see Bank
 */
public class BankRepository extends Repository<Bank> {

    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM banks WHERE active = TRUE AND id = ?";
    private static final String SELECT_BY_ACCOUNT_ID_QUERY = """
        SELECT * FROM banks b
            LEFT JOIN accounts a on a.bank_id = b.id
        WHERE b.active = TRUE AND a.id = ?
    """;
    private static final String SELECT_ALL_QUERY = "SELECT * FROM banks WHERE active = TRUE LIMIT ? OFFSET ?";
    private static final String INSERT_QUERY = "INSERT INTO banks (name, active) VALUES (?, ?)";
    private static final String UPDATE_QUERY = "UPDATE banks SET name = ?, active = ? WHERE id = ?";
    private static final String DELETE_QUERY = "UPDATE banks SET active = FALSE WHERE id = ?";
    private static final String DELETE_BANK_ACCOUNTS_QUERY = "UPDATE accounts SET active = FALSE WHERE bank_id = ?";

    public BankRepository(DataSource dataSource) {
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
    protected Bank construct(ResultSet resultSet) throws SQLException {
        Bank bank = new Bank();
        bank.setId(resultSet.getLong("id"));
        bank.setName(resultSet.getString("name"));
        bank.setActive(resultSet.getBoolean("active"));
        return bank;
    }

    @Override
    protected void settingPreparedStatement(PreparedStatement preparedStatement, Bank element) throws SQLException {
        preparedStatement.setString(1, element.getName());
        preparedStatement.setBoolean(2, true);
    }

    /**
     * Retrieves a bank associated with a given account ID.
     *
     * @param accountId The ID of the account to find the associated bank.
     * @return An optional containing the bank associated with the account, or empty if not found.
     * @throws RepositoryException If there is an error during the repository operation.
     */
    public Optional<Bank> findByAccountId(Long accountId) throws RepositoryException {
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_ACCOUNT_ID_QUERY)
        ) {
            preparedStatement.setLong(1, accountId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? Optional.of(construct(resultSet)) : Optional.empty();
            }
        } catch (Exception ex) {
            throw new RepositoryException("The entity was not found[" + ex.getMessage() + "]");
        }
    }

    @Override
    protected void deleteLinks(Connection connection, Long id) throws SQLException {
        deleteBankAccounts(connection, id);
    }

    private void deleteBankAccounts(Connection connection, Long userId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BANK_ACCOUNTS_QUERY)) {
            preparedStatement.setLong(1, userId);
            preparedStatement.executeUpdate();
        }
    }
}
