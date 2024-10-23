package org.example.services;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.example.Frequency;
import org.example.database.DatabaseConnection;
import org.example.entities.Habit;
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
import java.util.List;



@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HabitServiceTest {

    private PostgreSQLContainer<?> postgresContainer;
    private DatabaseConnection databaseConnection;
    private UserRepository userRepository;
    private HabitRepository habitRepository;
    private UserService userService;
    private HabitService habitService;
    private User testUser;
    private Habit habit;


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
        habitService = new HabitService(userService, habitRepository);


        testUser = new User(1L,"Тест", "test@example.com", "password");
        userRepository.save(testUser);

        habit = new Habit("Привычка", "Описание", Frequency.DAILY, 1L);


        try (Connection connection = databaseConnection.getConnection()) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("db/changelog/changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update("");
        } catch (SQLException | LiquibaseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateHabit() {
        habitRepository.save(habit);
        List<Habit> habits = habitRepository.findAll(testUser.getId());
        Assertions.assertEquals(1, habits.size(), "Должна быть одна привычка.");
        Assertions.assertEquals("Привычка", habits.get(0).getName(), "Имя привычки должно совпадать.");

    }

    @Test
    public void testEditHabit() {
        habitRepository.save(habit);
        habit.setName("Новое имя");
        habitRepository.update(habit);

        Habit editedHabit = habitRepository.findById(habit.getId(), testUser.getId());
        Assertions.assertEquals("Новое имя", editedHabit.getName(), "Имя привычки должно быть обновлено.");
    }

    @Test
    public void testDeleteHabit() {
        habitRepository.save(habit);
        habitRepository.delete(habit.getId());

        Habit deletedHabit = habitRepository.findById(habit.getId(), testUser.getId());
        Assertions.assertNull(deletedHabit, "Привычка должна быть удалена.");
    }

    @Test
    public void testFindAllHabits() {
        Habit habit1 = new Habit("Чтение", "Чтение книг", Frequency.DAILY, testUser.getId());
        Habit habit2 = new Habit("Спорт", "Заниматься спортом", Frequency.WEEKLY, testUser.getId());

        habitRepository.save(habit1);
        habitRepository.save(habit2);

        List<Habit> habits = habitRepository.findAll(testUser.getId());
        Assertions.assertEquals(2, habits.size(), "Должно быть 2 привычки.");
    }

    @AfterAll
    public void tearDown() {
        postgresContainer.stop();
    }
}
