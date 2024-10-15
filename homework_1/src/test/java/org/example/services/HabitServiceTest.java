package org.example.services;

import org.example.entities.ExecutionRecord;
import org.example.entities.Habit;
import org.example.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class HabitServiceTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("TestUser", "test@example.com", "password");
    }

    @Test
    void createHabit_shouldAddHabitToUser() {
        String name = "Exercise";
        String description = "Daily workout";
        String frequency = "Ежедневно";
        user.addHabit(name, description, frequency);

        assertThat(user.getHabits()).hasSize(1);
        assertThat(user.getHabits().get(0).getName()).isEqualTo(name);
    }

    @Test
    void editHabit_shouldChangeHabitDetails() {
        String name = "Exercise";
        String description = "Daily workout";
        String frequency = "Ежедневно";
        user.addHabit(name, description, frequency);

        Habit habitToEdit = user.getHabitByIndex(0);
        habitToEdit.setName("Running");
        habitToEdit.setDescription("Morning run");

        assertThat(user.getHabitByIndex(0).getName()).isEqualTo("Running");
        assertThat(user.getHabitByIndex(0).getDescription()).isEqualTo("Morning run");
    }

    @Test
    void deleteHabit_shouldRemoveHabitFromUser() {
        user.addHabit("Exercise", "Daily workout", "Ежедневно");

        user.deleteHabit(0);

        assertThat(user.getHabits()).isEmpty();
    }

    @Test
    void markHabitAsDone_shouldUpdateExecutionRecord() {
        user.addHabit("Exercise", "Daily workout", "Ежедневно");

        user.markHabitAsDone(user, 0);

        Habit habit = user.getHabitByIndex(0);
        assertThat(habit.getHistory()).hasSize(1);
        assertThat(habit.getHistory().get(0).isDone()).isTrue();
        assertThat(habit.getHistory().get(0).getDate()).isEqualTo(LocalDate.now());
    }
}
