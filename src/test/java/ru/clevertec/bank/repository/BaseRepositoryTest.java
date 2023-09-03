package ru.clevertec.bank.repository;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.BeforeAll;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Testcontainers
abstract class BaseRepositoryTest {

    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    protected DataSource dataSource;

    public BaseRepositoryTest() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(postgreSQLContainer.getJdbcUrl());
        dataSource.setUser(postgreSQLContainer.getUsername());
        dataSource.setPassword(postgreSQLContainer.getPassword());
        dataSource.setDatabaseName(postgreSQLContainer.getDatabaseName());
        this.dataSource = dataSource;
    }

    @BeforeAll
    static void start() throws LiquibaseException, SQLException {
        postgreSQLContainer.start();
        Connection connection = DriverManager.getConnection(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword()
        );
        Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));
        Liquibase liquibase = new Liquibase
                ("databases\\banking\\changelog.xml",
                        new ClassLoaderResourceAccessor(),
                        database
                );
        Contexts contexts = new Contexts();
        contexts.add("data");
        contexts.add("test_data");
        liquibase.update(contexts, new LabelExpression());
    }
}
