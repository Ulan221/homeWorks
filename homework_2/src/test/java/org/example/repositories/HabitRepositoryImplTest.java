package org.example.repositories;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.example.Frequency;
import org.example.database.DatabaseConnection;
import org.example.entities.Habit;
import org.example.entities.ExecutionRecord;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HabitRepositoryImplTest {

    private PostgreSQLContainer<?> postgresContainer;
    private DatabaseConnection databaseConnection;
    private HabitRepositoryImpl habitRepository;

    @BeforeAll
    public void setUp() {
        postgresContainer = new PostgreSQLContainer<>("postgres:latest")
                .withDatabaseName("test_db")
                .withUsername("ulan")
                .withPassword("11111");
        postgresContainer.start();

        String jdbcUrl = postgresContainer.getJdbcUrl();
        databaseConnection = new DatabaseConnection(jdbcUrl, postgresContainer.getUsername(), postgresContainer.getPassword());
        habitRepository = new HabitRepositoryImpl(databaseConnection);

        // Применяем Liquibase миграции
        try (Connection connection = databaseConnection.getConnection()) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("db/changelog/changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update("");
        } catch (SQLException | LiquibaseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSaveHabit() {
        Habit habit = new Habit();
        habit.setName("Упражнения");
        habit.setDescription("Ежедневная утренняя зарядка");
        habit.setFrequency(Frequency.DAILY);
        habit.setCreatedDate(LocalDate.now());
        habit.setUserId(1L);

        habitRepository.save(habit);

        assertNotNull(habit.getId(), "ID привычки должен генерироваться после создания");
    }

    @Test
    public void testUpdateHabit() {
        Habit habit = new Habit();
        habit.setName("Чтение");
        habit.setDescription("Чтение книги 30 минут");
        habit.setFrequency(Frequency.DAILY);
        habit.setCreatedDate(LocalDate.now());
        habit.setUserId(1L);

        habitRepository.save(habit);

        habit.setName("Обновленное чтение");
        habit.setDescription("Чтение книги 1 час");

        habitRepository.update(habit);

        Habit updatedHabit = habitRepository.findById(habit.getId(), habit.getUserId());
        assertEquals("Обновленное чтение", updatedHabit.getName());
        assertEquals("Чтение книги 1 час", updatedHabit.getDescription());
    }

    @Test
    public void testDeleteHabit() {
        Habit habit = new Habit();
        habit.setName("Медитация");
        habit.setDescription("Утренняя медитация");
        habit.setFrequency(Frequency.DAILY);
        habit.setCreatedDate(LocalDate.now());
        habit.setUserId(1L);

        habitRepository.save(habit);

        habitRepository.delete(habit.getId());

        Habit deletedHabit = habitRepository.findById(habit.getId(), habit.getUserId());
        assertNull(deletedHabit, "Привычка должна быть удалена из базы данных");
    }

    @Test
    public void testFindAllHabits() {
        Habit habit1 = new Habit();
        habit1.setName("Упражнения");
        habit1.setDescription("Утренняя зарядка");
        habit1.setFrequency(Frequency.DAILY);
        habit1.setCreatedDate(LocalDate.now());
        habit1.setUserId(1L);

        Habit habit2 = new Habit();
        habit2.setName("Медитация");
        habit2.setDescription("Вечерняя медитация");
        habit2.setFrequency(Frequency.DAILY);
        habit2.setCreatedDate(LocalDate.now());
        habit2.setUserId(1L);

        habitRepository.save(habit1);
        habitRepository.save(habit2);

        List<Habit> habits = habitRepository.findAll(1L);
        assertEquals(8, habits.size(), "Должны быть добавлены 8 привычек");
    }

    @Test
    public void testMarkAsDone() {
        Habit habit = new Habit();
        habit.setName("Упражнения");
        habit.setDescription("Утренняя зарядка");
        habit.setFrequency(Frequency.DAILY);
        habit.setCreatedDate(LocalDate.now());
        habit.setUserId(1L);

        habitRepository.save(habit);

        LocalDate today = LocalDate.now();
        habitRepository.markAsDone(habit.getId(), today);

        int streak = habitRepository.calculateStreak(habit);
        assertEquals(1, streak, "Серия должна быть равна 1 после отметки выполнения");
    }

    @Test
    public void testCalculateStreak() {
        Habit habit = new Habit();
        habit.setName("Упражнения");
        habit.setDescription("Утренняя зарядка");
        habit.setFrequency(Frequency.DAILY);
        habit.setCreatedDate(LocalDate.now());
        habit.setUserId(1L);

        habitRepository.save(habit);

        LocalDate today = LocalDate.now();
        habitRepository.markAsDone(habit.getId(), today);
        habitRepository.markAsDone(habit.getId(), today.minusDays(1));
        habitRepository.markAsDone(habit.getId(), today.minusDays(2));

        int streak = habitRepository.calculateStreak(habit);
        assertEquals(3, streak, "Серия должна быть равна 3 после трех последовательных дней выполнения");
    }

    @Test
    public void testCalculateSuccessRate() {
        Habit habit = new Habit();
        habit.setName("Упражнения");
        habit.setDescription("Утренняя зарядка");
        habit.setFrequency(Frequency.DAILY);
        habit.setCreatedDate(LocalDate.now());
        habit.setUserId(1L);

        habitRepository.save(habit);

        LocalDate today = LocalDate.now();
        habitRepository.markAsDone(habit.getId(), today);
        habitRepository.markAsDone(habit.getId(), today.minusDays(1));

        double successRate = habitRepository.calculateSuccessRate(habit, today.minusDays(5), today);
        assertTrue(successRate > 0, "Коэффициент успешности должен быть корректно рассчитан");
    }

    @Test
    public void testGetExecutionHistoryForPeriod() {
        Habit habit = new Habit();
        habit.setName("Упражнения");
        habit.setDescription("Утренняя зарядка");
        habit.setFrequency(Frequency.DAILY);
        habit.setCreatedDate(LocalDate.now());
        habit.setUserId(1L);


        habitRepository.save(habit);

        LocalDate today = LocalDate.now();
        habitRepository.markAsDone(habit.getId(), today);
        habitRepository.markAsDone(habit.getId(), today.minusDays(1));


        List<ExecutionRecord> history = habitRepository.getExecutionHistoryForPeriod(habit, today.minusDays(2), today);
        assertEquals(2, history.size(), "Должны быть 2 записи о выполнении за указанный период");
    }

    @AfterAll
    public void tearDown() {
        postgresContainer.stop();
    }
}
