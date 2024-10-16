package org.example.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import java.util.List;
import java.util.Arrays;

class HabitTest {
    private Habit habit;

    @BeforeEach
    void setUp() {
        habit = new Habit("Привычка", "Описание привычки", "Ежедневно", LocalDate.now());
    }

    @Test
    void markAsDone_WhenRecordExists_ShouldUpdateExistingRecord() {
        LocalDate testDate = LocalDate.now();
        habit.markAsDone(testDate);
        ExecutionRecord record = habit.getHistory().stream()
                .filter(r -> r.getDate().equals(testDate))
                .findFirst()
                .orElse(null);

        assertThat(record).isNotNull();
        assertThat(record.isDone()).isTrue();
    }

    @Test
    void markAsDone_WhenRecordDoesNotExist_ShouldCreateNewRecord() {
        LocalDate testDate = LocalDate.now().plusDays(1);
        habit.markAsDone(testDate);
        ExecutionRecord record = habit.getHistory().stream()
                .filter(r -> r.getDate().equals(testDate))
                .findFirst()
                .orElse(null);

        assertThat(record).isNotNull();
        assertThat(record.isDone()).isTrue();
    }

    @Test
    void sortByName() {
        Habit habit1 = new Habit("Первая привычка", "Описание", "Ежедневно", LocalDate.now());
        Habit habit2 = new Habit("Вторая привычка", "Описание", "Ежедневно", LocalDate.now());
        Habit habit3 = new Habit("Третья привычка", "Описание", "Ежедневно", LocalDate.now());

        List<Habit> habits = Arrays.asList(habit1, habit2, habit3);
        habits.sort(Habit.sortByName);
        assertThat(habits.get(0).getName()).isEqualTo("Вторая привычка");
        assertThat(habits.get(1).getName()).isEqualTo("Первая привычка");
        assertThat(habits.get(2).getName()).isEqualTo("Третья привычка");
    }

    @Test
    void sortByDate() {
        Habit habit1 = new Habit("Первая привычка", "Описание", "Ежедневно", LocalDate.now().minusDays(2));
        Habit habit2 = new Habit("Вторая привычка", "Описание", "Ежедневно", LocalDate.now().minusDays(3));
        Habit habit3 = new Habit("Третья привычка", "Описание", "Ежедневно", LocalDate.now());

        List<Habit> habits = Arrays.asList(habit1, habit2, habit3);
        habits.sort(Habit.sortByDate);
        assertThat(habits.get(0)).isEqualTo(habit2);
        assertThat(habits.get(1)).isEqualTo(habit1);
        assertThat(habits.get(2)).isEqualTo(habit3);
    }


    @Test
    void testToString() {
        String expectedString = "Привычка: Привычка, Описание: Описание привычки, Частота: Ежедневно";
        assertThat(habit.toString()).isEqualTo(expectedString);
    }

    @Test
    void getName() {
        assertThat(habit.getName()).isEqualTo("Привычка");
        habit.setName("Новое имя привычки");
        assertThat(habit.getName()).isEqualTo("Новое имя привычки");
    }

    @Test
    void getDescription() {
        assertThat(habit.getDescription()).isEqualTo("Описание привычки");
        habit.setDescription("Новое описание привычки");
        assertThat(habit.getDescription()).isEqualTo("Новое описание привычки");
    }

    @Test
    void getFrequency() {
        assertThat(habit.getFrequency()).isEqualTo("Ежедневно");
        habit.setFrequency("Еженедельно");
        assertThat(habit.getFrequency()).isEqualTo("Еженедельно");
    }

    @Test
    void getCreatedDate() {
        assertThat(habit.getCreatedDate()).isEqualTo(LocalDate.now());
        LocalDate newDate = LocalDate.now().minusDays(2);
        habit.setCreatedDate(newDate);
        assertThat(habit.getCreatedDate()).isEqualTo(newDate);
    }
}