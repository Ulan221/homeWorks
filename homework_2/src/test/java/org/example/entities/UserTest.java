package org.example.entities;

import org.example.Frequency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class UserTest {

    @Test
    public void testConstructorAndGetters() {
        String name = "Тест";
        String email = "test@example.com";
        String password = "password";

        User user = new User(name, email, password);

        Assertions.assertEquals(name, user.getName(), "Имя пользователя должно совпадать.");
        Assertions.assertEquals(email, user.getEmail(), "Email пользователя должен совпадать.");
        Assertions.assertEquals(password, user.getPassword(), "Пароль пользователя должен совпадать.");
        Assertions.assertNotNull(user.getHabits(), "Список привычек не должен быть null.");
        Assertions.assertTrue(user.getHabits().isEmpty(), "Список привычек должен быть пустым.");
    }

    @Test
    public void testConstructorWithId() {
        Long id = 1L;
        String name = "Тест";
        String email = "test@example.com";
        String password = "password";

        User user = new User(id, name, email, password);

        Assertions.assertEquals(id, user.getId(), "ID пользователя должен совпадать.");
        Assertions.assertEquals(name, user.getName(), "Имя пользователя должно совпадать.");
        Assertions.assertEquals(email, user.getEmail(), "Email пользователя должен совпадать.");
        Assertions.assertEquals(password, user.getPassword(), "Пароль пользователя должен совпадать.");
    }

    @Test
    public void testAddHabit() {
        User user = new User("Тест", "test@example.com", "password");
        Habit habit = new Habit("Чтение", "Чтение книг", Frequency.DAILY, user.getId());

        user.getHabits().add(habit);

        Assertions.assertEquals(1, user.getHabits().size(), "Список привычек должен содержать 1 привычку.");
        Assertions.assertEquals(habit, user.getHabits().get(0), "Первая привычка должна быть 'Чтение'.");
    }

    @Test
    public void testToString() {
        User user = new User("Тест", "test@example.com", "password");
        String expectedString = "Пользователь: Тест, email: test@example.com";

        Assertions.assertEquals(expectedString, user.toString(), "Строковое представление пользователя должно совпадать.");
    }

    @Test
    public void testUserHabitsInitialization() {
        User user = new User("Тест", "test@example.com", "password");
        List<Habit> habits = user.getHabits();

        Assertions.assertNotNull(habits, "Список привычек не должен быть null.");
        Assertions.assertTrue(habits.isEmpty(), "Список привычек должен быть пустым при инициализации.");
    }
}
