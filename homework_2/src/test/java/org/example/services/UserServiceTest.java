package org.example.services;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.example.database.DatabaseConnection;
import org.example.entities.User;
import org.example.in.ConsoleUI;
import org.example.repositories.HabitRepository;
import org.example.repositories.HabitRepositoryImpl;
import org.example.repositories.UserRepository;
import org.example.repositories.UserRepositoryImpl;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {

    private PostgreSQLContainer<?> postgresContainer;
    private DatabaseConnection databaseConnection;
    private UserRepository userRepository;
    private HabitRepository habitRepository;
    private UserService userService;
    private User testUser;

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
        habitRepository = new HabitRepositoryImpl(databaseConnection);
        userService = new UserService(userRepository, habitRepository);

        testUser = new User(1L, "Тест", "test@example.com", "password");
        userRepository.save(testUser);

        try (Connection connection = databaseConnection.getConnection()) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("db/changelog/changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update("");
        } catch (SQLException | LiquibaseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRegisterUser() {
        User newUser = new User("Новый", "new@example.com", "newpassword");
        userRepository.save(newUser);

        Optional<User> savedUser = userRepository.findByEmail(newUser.getEmail());
        Assertions.assertTrue(savedUser.isPresent(), "Пользователь должен быть зарегистрирован.");
        Assertions.assertEquals(newUser.getName(), savedUser.get().getName(), "Имя пользователя должно совпадать.");
    }

    @Test
    public void testLoginSuccessful() {
        String email = "test@example.com";
        String password = "password";
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                System.out.println("Вход успешен");
            } else {
                System.out.println("Неверный пароль");
            }
        } else {
            System.out.println("Такого пользователя нет");
        }
    }

    @Test
    public void testEditAccount() {
        String newName = "Новое Имя";
        String newEmail = "new@example.com";
        String newPassword = "newpassword";

        testUser.setName(newName);
        testUser.setEmail(newEmail);
        testUser.setPassword(newPassword);
        userRepository.update(testUser);

        Optional<User> updatedUser = userRepository.findByEmail(newEmail);
        Assertions.assertTrue(updatedUser.isPresent(), "Пользователь должен существовать после обновления.");
        Assertions.assertEquals(newName, updatedUser.get().getName(), "Имя пользователя должно быть обновлено.");
    }

    @AfterAll
    public void tearDown() {
        postgresContainer.stop();
    }
}
