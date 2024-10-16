package org.example.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Тестовое имя", "test@example.com", "password123");
    }

    @Test
    void addHabit_ShouldAddHabitToUser() {
        user.addHabit("Привычка 1", "Описание 1", "Ежедневно");

        assertThat(user.getHabits()).hasSize(1);
        assertThat(user.getHabitByIndex(0).getName()).isEqualTo("Привычка 1");
    }

    @Test
    void deleteHabit_ShouldRemoveHabit() {
        user.addHabit("Привычка 1", "Описание 1", "Ежедневно");
        user.deleteHabit(0);

        assertThat(user.getHabits()).isEmpty();
    }

    @Test
    void deleteHabit_InvalidIndex_ShouldNotRemoveHabit() {
        user.addHabit("Привычка 1", "Описание 1", "Ежедневно");
        user.deleteHabit(1);

        assertThat(user.getHabits()).hasSize(1);
    }

    @Test
    void getHabitByIndex_ValidIndex_ShouldReturnHabit() {
        user.addHabit("Привычка 1", "Описание 1", "Ежедневно");

        Habit habit = user.getHabitByIndex(0);
        assertThat(habit).isNotNull();
        assertThat(habit.getName()).isEqualTo("Привычка 1");
    }

    @Test
    void getHabitByIndex_InvalidIndex_ShouldReturnNull() {
        assertThat(user.getHabitByIndex(0)).isNull();
    }

    @Test
    void markHabitAsDone_ShouldMarkHabitAsDone() {
        user.addHabit("Привычка 1", "Описание 1", "Ежедневно");
        user.markHabitAsDone(user, 0);

        Habit habit = user.getHabitByIndex(0);
        assertThat(habit.getHistory()).isNotEmpty();
        assertThat(habit.getHistory().get(0).isDone()).isTrue();
    }

    @Test
    void markHabitAsDone_InvalidIndex_ShouldDoNothing() {
        user.addHabit("Привычка 1", "Описание 1", "Ежедневно");
        user.markHabitAsDone(user, 1);

        Habit habit = user.getHabitByIndex(0);
        assertThat(habit.getHistory()).isEmpty();
    }


    @Test
    void testToString() {
        String expectedString = "Пользователь: Тестовое имя, email: test@example.com";
        assertThat(user.toString()).isEqualTo(expectedString);
    }
}
