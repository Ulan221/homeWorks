package org.example.entities;

import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String name;
    private String email;
    private String password;
    private List<Habit> habits = new ArrayList<>();

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Habit getHabitByIndex(int index) {
        if(index >= 0 && index < habits.size()) {
            return habits.get(index);
        } else {
            System.out.println("Неправильный индекс");
            return null;
        }
    }

    public void showHabitList() {
        System.out.println();
        for (int i = 0; i < habits.size(); i++) {
            System.out.println(i + 1 + ". " + habits.get(i));
        }
    }

    public void addHabit(String name, String description, String frequency) {
        Habit habit = new Habit(name, description, frequency, LocalDate.now());
        habits.add(habit);
        System.out.println("Привычка добавлена");
    }


    public void deleteHabit(int index) {
        if (index >= 0 && index < habits.size()) {
            habits.remove(habits.get(index));
            System.out.println("Привычка удалена");
        } else {
            System.out.println("Неправильно введен номер");
        }
    }

    public void markHabitAsDone(User user, int habitIndex) {
        Habit habit = user.getHabitByIndex(habitIndex);
        if (habit != null) { // Проверка на null
            LocalDate today = LocalDate.now();
            habit.markAsDone(today);
            System.out.println("Привычка отмечена как выполненная.");
        } else {
            System.out.println("Невозможно отметить привычку, так как она не найдена.");
        }
    }


    @Override
    public String toString() {
        return "Пользователь: " + name + ", email: " + email;
    }
}
