package org.example.entities;

import org.example.Frequency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HabitTest {

    @Test
    public void testConstructorAndGetters() {
        String name = "Чтение";
        String description = "Чтение книг";
        Frequency frequency = Frequency.DAILY;
        Long userId = 1L;

        Habit habit = new Habit(name, description, frequency, userId);

        Assertions.assertEquals(name, habit.getName(), "Имя привычки должно совпадать.");
        Assertions.assertEquals(description, habit.getDescription(), "Описание привычки должно совпадать.");
        Assertions.assertEquals(frequency, habit.getFrequency(), "Частота привычки должна совпадать.");
        Assertions.assertEquals(userId, habit.getUserId(), "ID пользователя должен совпадать.");
        Assertions.assertEquals(LocalDate.now(), habit.getCreatedDate(), "Дата создания должна быть сегодня.");
        Assertions.assertNotNull(habit.getHistory(), "История выполнения не должна быть null.");
        Assertions.assertTrue(habit.getHistory().isEmpty(), "История выполнения должна быть пустой.");
    }

    @Test
    public void testMarkAsDone() {
        Habit habit = new Habit("Чтение", "Чтение книг", Frequency.DAILY, 1L);
        LocalDate today = LocalDate.now();

        habit.markAsDone(today);

        Assertions.assertEquals(1, habit.getHistory().size(), "История выполнения должна содержать 1 запись.");
        Assertions.assertTrue(habit.getHistory().get(0).isDone(), "Первая запись в истории должна быть выполнена.");

        // Попробуем отметить привычку как выполненную еще раз
        habit.markAsDone(today);
        Assertions.assertEquals(1, habit.getHistory().size(), "История выполнения должна по-прежнему содержать 1 запись.");
    }

    @Test
    public void testSortByName() {
        Habit habit1 = new Habit("Чтение", "Чтение книг", Frequency.DAILY, 1L);
        Habit habit2 = new Habit("Спорт", "Занятия спортом", Frequency.WEEKLY, 1L);

        List<Habit> habits = new ArrayList<>();
        habits.add(habit2);
        habits.add(habit1);

        Collections.sort(habits, Habit.sortByName);

        Assertions.assertEquals(habit1, habits.get(0), "Первым элементом должен быть 'Чтение'.");
        Assertions.assertEquals(habit2, habits.get(1), "Вторым элементом должен быть 'Спорт'.");
    }

    @Test
    public void testSortByDate() {
        Habit habit1 = new Habit("Чтение", "Чтение книг", Frequency.DAILY, 1L);
        habit1.setCreatedDate(LocalDate.of(2024, 10, 23));

        Habit habit2 = new Habit("Спорт", "Занятия спортом", Frequency.WEEKLY, 1L);
        habit2.setCreatedDate(LocalDate.of(2024, 10, 24));

        List<Habit> habits = new ArrayList<>();
        habits.add(habit2);
        habits.add(habit1);

        Collections.sort(habits, Habit.sortByDate);

        Assertions.assertEquals(habit1, habits.get(0), "Первым элементом должен быть 'Чтение', так как оно было создано раньше.");
        Assertions.assertEquals(habit2, habits.get(1), "Вторым элементом должен быть 'Спорт'.");
    }

    @Test
    public void testToString() {
        Habit habit = new Habit("Чтение", "Чтение книг", Frequency.DAILY, 1L);
        String expected = "Чтение, Описание: Чтение книг, Частота: DAILY, Дата создания: " + LocalDate.now();

        Assertions.assertTrue(habit.toString().contains("Чтение"), "Строковое представление должно содержать имя привычки.");
        Assertions.assertTrue(habit.toString().contains("Чтение книг"), "Строковое представление должно содержать описание.");
        Assertions.assertTrue(habit.toString().contains("DAILY"), "Строковое представление должно содержать частоту.");
    }
}
