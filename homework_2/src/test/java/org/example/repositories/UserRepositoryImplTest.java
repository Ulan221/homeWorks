package org.example.repositories;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.example.database.DatabaseConnection;
import org.example.entities.User;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserRepositoryImplTest {

    private PostgreSQLContainer<?> postgresContainer;
    private DatabaseConnection databaseConnection;
    private UserRepositoryImpl userRepository;

    @BeforeAll
    public void setUp() {
        postgresContainer = new PostgreSQLContainer<>("postgres:latest")
                .withDatabaseName("test_db")
                .withUsername("ulan")
                .withPassword("11111");
        postgresContainer.start();

        String jdbcUrl = postgresContainer.getJdbcUrl();
        databaseConnection = new DatabaseConnection(jdbcUrl, postgresContainer.getUsername(), postgresContainer.getPassword());
        userRepository = new UserRepositoryImpl(databaseConnection);

        try (Connection connection = databaseConnection.getConnection()) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("db/changelog/changelog.xml", new ClassLoaderResourceAccessor(), database);

            liquibase.update("");
        } catch (SQLException | LiquibaseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setName("John");
        user.setEmail("John@example.com");
        user.setPassword("111111");

        userRepository.save(user);

        Assertions.assertNotNull(user.getId(), "User ID должно генерироватся до создания");
    }


    @Test
    public void testFindByEmail() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setPassword("alicePass");

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("alice@example.com");
        Assertions.assertTrue(foundUser.isPresent(), "Пользователь должен быть найден по email");
        Assertions.assertEquals("Alice", foundUser.get().getName(), "Имя пользователя должно совпадать с сохраненным");
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setName("Bob");
        user.setEmail("bob@example.com");
        user.setPassword("bobPass");

        userRepository.save(user);

        user.setName("Robert");
        userRepository.update(user);

        Optional<User> updatedUser = userRepository.findByEmail("bob@example.com");
        Assertions.assertTrue(updatedUser.isPresent(), "Обновленный пользователь должен быть найден по email");
        Assertions.assertEquals("Robert", updatedUser.get().getName(), "Имя пользователя должно быть обновлено");
    }

    @Test
    public void testDeleteUserById() {
        User user = new User();
        user.setName("Charlie");
        user.setEmail("charlie@example.com");
        user.setPassword("charliePass");

        userRepository.save(user);

        Assertions.assertNotNull(user.getId(), "ID пользователя должен быть сгенерирован после сохранения");

        userRepository.deleteById(user.getId());

        Optional<User> deletedUser = userRepository.findByEmail("charlie@example.com");
        Assertions.assertTrue(deletedUser.isEmpty(), "Пользователь должен быть удален и не найден");
    }

    @Test
    public void testFindAllUsers() {
        User user1 = new User();
        user1.setName("Dan");
        user1.setEmail("dan@example.com");
        user1.setPassword("danPass");

        User user2 = new User();
        user2.setName("Eve");
        user2.setEmail("eve@example.com");
        user2.setPassword("evePass");

        userRepository.save(user1);
        userRepository.save(user2);

        List<User> users = userRepository.findAll();

        Assertions.assertEquals(4, users.size(), "В базе данных должно быть 4 пользователя");
    }

    @AfterAll
    public void tearDown() {
        postgresContainer.stop();
    }
}