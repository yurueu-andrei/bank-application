package ru.clevertec.bank.repository;

import ru.clevertec.bank.exception.RepositoryException;
import ru.clevertec.bank.entity.BaseEntity;
import lombok.Getter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Base repository class for managing database operations on entities.
 *
 * @param <E> The type of entity managed by the repository.
 * @author Andrei Yuryeu
 */
@Getter
public abstract class Repository<E extends BaseEntity> {

    private final DataSource dataSource;

    public Repository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected abstract String getSelectByIdQuery();

    protected abstract String getSelectAllQuery();

    protected abstract String getInsertQuery();

    protected abstract String getUpdateQuery();

    protected abstract String getDeleteQuery();

    protected abstract E construct(ResultSet resultSet) throws SQLException;

    protected abstract void settingPreparedStatement(PreparedStatement preparedStatement, E element) throws SQLException;

    /**
     * Retrieves an entity by its unique identifier (id).
     *
     * @param id The unique identifier (id) of the entity to retrieve.
     * @return An Optional containing the retrieved entity if found, or empty if not found.
     * @throws RepositoryException If there is an error during the repository operation.
     */
    public Optional<E> findById(Long id) throws RepositoryException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getSelectByIdQuery())
        ) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? Optional.of(construct(resultSet)) : Optional.empty();
            }
        } catch (Exception ex) {
            throw new RepositoryException("The entity was not found[" + ex.getMessage() + "]");
        }
    }

    /**
     * Retrieves a list of entities with optional limits and offsets.
     *
     * @param limit  The maximum number of entities to retrieve.
     * @param offset The offset to start retrieving entities.
     * @return A list of retrieved entities.
     * @throws RepositoryException If there is an error during the repository operation.
     */
    public List<E> findAll(int limit, int offset) throws RepositoryException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getSelectAllQuery())
        ) {
            preparedStatement.setLong(1, limit);
            preparedStatement.setLong(2, offset);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<E> found = new ArrayList<>();
                while (resultSet.next()) {
                    found.add(construct(resultSet));
                }
                return found;
            }
        } catch (Exception ex) {
            throw new RepositoryException("The entities were not found[" + ex.getMessage() + "]");
        }
    }

    /**
     * Saves an entity to the repository.
     *
     * @param element The entity to be saved.
     * @return The saved entity with an assigned id.
     * @throws RepositoryException If there is an error during the repository operation.
     */
    public E save(E element) throws RepositoryException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     getInsertQuery(),
                     Statement.RETURN_GENERATED_KEYS
             )
        ) {
            settingPreparedStatement(preparedStatement, element);
            int value = preparedStatement.executeUpdate();
            if (value == 1) {
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        element.setId(resultSet.getLong(1));
                    }
                }
            }
            return element;
        } catch (Exception ex) {
            throw new RepositoryException(element.getClass().getSimpleName() + " was not added [" + ex.getMessage() + "]");
        }
    }

    /**
     * Updates an existing entity in the repository.
     *
     * @param element The entity to be updated.
     * @return True if the entity was successfully updated, false otherwise.
     * @throws RepositoryException If there is an error during the repository operation.
     */
    public boolean update(E element) throws RepositoryException {
        int idQueryIndex = findIdPosition(getUpdateQuery());
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getUpdateQuery())
        ) {
            settingPreparedStatement(preparedStatement, element);
            preparedStatement.setLong(idQueryIndex, element.getId());
            return preparedStatement.executeUpdate() == 1;
        } catch (Exception ex) {
            throw new RepositoryException(element.getClass().getSimpleName() + " was not updated [" + ex.getMessage() + "]");
        }
    }

    /**
     * Utility method to find the position of '?' placeholders in a SQL query.
     *
     * @param query The SQL query string.
     * @return The count of '?' placeholders in the query.
     */
    private int findIdPosition(String query) {
        return (int) query.chars()
                .filter(charId -> charId == '?')
                .count();
    }

    /**
     * Deletes an entity from the repository by its unique identifier (id).
     *
     * @param id The unique identifier (id) of the entity to delete.
     * @return True if the entity was successfully deleted, false otherwise.
     * @throws RepositoryException If there is an error during the repository operation.
     */
    public boolean delete(Long id) throws RepositoryException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(getDeleteQuery())
        ) {
            preparedStatement.setLong(1, id);
            try {
                connection.setAutoCommit(false);
                deleteLinks(connection, id);
                preparedStatement.executeUpdate();
                connection.commit();
            } catch (Exception ex) {
                connection.setAutoCommit(true);
                connection.rollback();
            }
            return true;
        } catch (Exception ex) {
            throw new RepositoryException("The entity was not deleted [" + ex.getMessage() + "]");
        }
    }

    /**
     * Method to delete links or related data in a custom repository.
     * Subclasses can override this method to provide specific behavior.
     *
     * @param connection The database connection.
     * @param id         The unique identifier (id) of the entity being deleted.
     * @throws SQLException If there is an error during the deletion of links.
     */
    protected void deleteLinks(Connection connection, Long id) throws SQLException {
    }
}
