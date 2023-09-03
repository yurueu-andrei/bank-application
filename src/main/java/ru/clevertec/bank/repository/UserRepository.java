package ru.clevertec.bank.repository;

import ru.clevertec.bank.exception.RepositoryException;
import ru.clevertec.bank.entity.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Repository class for managing operations on user entities.
 *
 * @author Andrei Yuryeu
 * @see Repository
 * @see User
 */
public class UserRepository extends Repository<User> {

    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM users WHERE active = TRUE AND id = ?";

    private static final String SELECT_BY_ACCOUNT_ID_QUERY = """
        SELECT * FROM users u
            LEFT JOIN accounts a on a.user_id = u.id
        WHERE u.active = TRUE AND a.id = ?
    """;

    private static final String SELECT_ALL_QUERY = "SELECT * FROM users WHERE active = TRUE LIMIT ? OFFSET ?";

    private static final String INSERT_QUERY = """
        INSERT INTO users (name, surname, birthdate, active) VALUES (?, ?, ?, ?)
    """;

    private static final String UPDATE_QUERY = """
        UPDATE users SET name = ?, surname = ?, birthdate = ?, active = ? WHERE id = ?
    """;

    private static final String DELETE_QUERY = "UPDATE users SET active = FALSE WHERE id = ?";

    private static final String DELETE_USER_ACCOUNTS_QUERY = """
        UPDATE accounts SET active = FALSE WHERE bank_id = 1 AND user_id = ?
    """;

    public UserRepository(DataSource dataSource) {
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
    protected User construct(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setName(resultSet.getString("name"));
        user.setSurname(resultSet.getString("surname"));
        user.setBirthdate(resultSet.getDate("birthdate").toLocalDate());
        user.setActive(resultSet.getBoolean("active"));
        return user;
    }

    @Override
    protected void settingPreparedStatement(PreparedStatement preparedStatement, User element) throws SQLException {
        preparedStatement.setString(1, element.getName());
        preparedStatement.setString(2, element.getSurname());
        preparedStatement.setDate(3, Date.valueOf(element.getBirthdate()));
        preparedStatement.setBoolean(4, true);
    }

    /**
     * Finds a user associated with a given account ID.
     *
     * @param accountId The ID of the account for which the user is to be found.
     * @return An {@link Optional} containing the user if found, otherwise empty.
     * @throws RepositoryException If there is an error during the repository operation.
     */
    public Optional<User> findByAccountId(Long accountId) throws RepositoryException {
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
        deleteUserAccounts(connection, id);
    }

    private void deleteUserAccounts(Connection connection, Long userId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_ACCOUNTS_QUERY)) {
            preparedStatement.setLong(1, userId);
            preparedStatement.executeUpdate();
        }
    }
}
