package org.example.services;

import org.example.Frequency;
import org.example.entities.ExecutionRecord;
import org.example.entities.Habit;
import org.example.entities.User;
import org.example.in.ConsoleUI;
import org.example.repositories.HabitRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

public class HabitService {
    private final UserService userService;
    private  final HabitRepository habitRepository;

    private static final String NAME_REGEX = "^[A-Za-zА-Яа-яЁё\\s]+$";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);
    private static final String INCORRECT_NAME_FORMAT_MESSAGE = "Некорректный формат названия.";

    public HabitService(UserService userService, HabitRepository habitRepository) {
        this.userService = userService;
        this.habitRepository = habitRepository;
    }

    public void habitsMenuManage(User user) {
        habitsMenu();
        habitsMenuChoice(user);

    }

    private void habitsMenu() {
        System.out.println();
        System.out.println("1. Создать привычку");
        System.out.println("2. Изменить привычку");
        System.out.println("3. Удаление привычки");
        System.out.println("4. Мои привычки");
        System.out.println("5. Отметить привычку активной");
        System.out.println("6. Статистика");
        System.out.println("7. Вернуться назад");
    }

    private void habitsMenuChoice(User user) {
        int habitChoice = ConsoleUI.promptInt("Введите число для выбора: ");

        switch (habitChoice) {
            case 1:
                createHabit(user);
                break;
            case 2:
                editHabit(user);
                break;
            case 3:
                deleteHabit(user);
                break;
            case 4:
                sortAndShowHabits(user);
                break;
            case 5:
                markHabit(user);
                break;
            case 6:
                habitsStatManage(user);
                break;
            case 7:
                userService.showUserMenu(user);

            default:
                System.out.println("Неправильный выбор, попробуйте снова.");
        }
        habitsMenuManage(user);
    }

    void createHabit(User user) {
        String name = ConsoleUI.prompt("Введите название привычки: ");
        String description = ConsoleUI.prompt("Введите описание привычки: ");
        Frequency frequency = chooseFrequency();

        if (!NAME_PATTERN.matcher(name).matches() || !NAME_PATTERN.matcher(description).matches()) {
            System.out.println(INCORRECT_NAME_FORMAT_MESSAGE);
            return;
        }

        if (frequency != null) {
            Habit habit = new Habit(name, description, frequency, user.getId());
            habitRepository.save(habit);
            System.out.println("Привычка добавлена");
        }
    }

    void editHabit(User user) {
        showHabits(user);
        long habitId = ConsoleUI.promptInt("Введите номер привычки для редактирования: ");

        Habit habit = habitRepository.findById(habitId, user.getId());
        if (habit == null) {
            System.out.println("Привычка не найдена.");
            return;
        }

        String newName = ConsoleUI.prompt("Введите новое имя (оставьте пустым для сохранения старого): ");
        String newDescription = ConsoleUI.prompt("Введите новое описание (оставьте пустым для сохранения старого): ");
        Frequency newFrequency = chooseFrequency();

        // Обновление полей привычки только если пользователь ввёл новые значения
        if (!newName.isEmpty() && NAME_PATTERN.matcher(newName).matches()) {
            habit.setName(newName);
        }
        if (!newDescription.isEmpty() && NAME_PATTERN.matcher(newDescription).matches()) {
            habit.setDescription(newDescription);
        }
        if (newFrequency != null) {
            habit.setFrequency(newFrequency);
        }

        habitRepository.update(habit);
        System.out.println("Привычка изменена");
    }



    void deleteHabit(User user) {
        showHabits(user);
        long indexDelete = ConsoleUI.promptInt("Введите номер привычки для удаления: ");
        habitRepository.delete(indexDelete);
        System.out.println("Привычка успешно удалена.");
    }


    private void sortAndShowHabits(User user) {
        showHabits(user);
        sortMenu();  // Вывод меню сортировки
        sortMenuChoice(user);  // Выбор опции сортировки
    }

    private void showHabits(User user){
        List<Habit> habits = habitRepository.findAll(user.getId());

        if (habits.isEmpty()) {
            System.out.println("У вас нет привычек.");
            return;
        }

        System.out.println("Список привычек:");
        System.out.printf("%-5s %-20s %-30s %-15s %-15s%n", "ID", "Название", "Описание", "Частота", "Дата создания");
        System.out.println("--------------------------------------------------------------------------------------");

        for (Habit habit : habits) {
            System.out.printf("%-5d %-20s %-30s %-15s %-15s%n",
                    habit.getId(),
                    habit.getName(),
                    habit.getDescription(),
                    habit.getFrequency().toString(),
                    habit.getCreatedDate().toString());
        }
    }


    private void sortMenu() {
        System.out.println();
        System.out.println("1. Сортировать по имени");
        System.out.println("2. Сортировать по дате добавления");
        System.out.println("3. Вернуться назад");
    }

    private void sortMenuChoice(User user) {
        List<Habit> habits = habitRepository.findAll(user.getId());

        int choiceSort = ConsoleUI.promptInt("Введите число для выбора: ");
        switch (choiceSort) {
            case 1:
                // Сортировка по имени
                habits.sort(Habit.sortByName);
                System.out.println("Привычки отсортированы по имени.");
                break;
            case 2:
                habits.sort(Habit.sortByDate);
                System.out.println("Привычки отсортированы по дате добавления.");
                break;
            case 3:
                return;
            default:
                System.out.println("Выберите один из предложенных вариантов.");
        }

        showSortedHabits(habits);
    }

    private void markHabit(User user) {
        showHabits(user);
        long indexToMark = ConsoleUI.promptInt("Введите номер привычки для отметки: ");
        habitRepository.findById(indexToMark, user.getId());
        markHabitAsDone(user, indexToMark);
    }

    public void markHabitAsDone(User user, long habitIndex) {
        Habit habit = habitRepository.findById(habitIndex, user.getId());
        if (habit != null) {
            LocalDate today = LocalDate.now();
            habitRepository.markAsDone(habit.getId(), today);
            System.out.println("Привычка отмечена как выполненная.");
        } else {
            System.out.println("Невозможно отметить привычку, так как она не найдена.");
        }
    }


    private void showSortedHabits(List<Habit> habits) {
        if (habits.isEmpty()) {
            System.out.println("Нет привычек для отображения.");
            return;
        }

        // Вывод отформатированного списка привычек
        System.out.println("Список привычек:");
        System.out.printf("%-5s %-20s %-30s %-15s %-15s%n", "ID", "Название", "Описание", "Частота", "Дата создания");
        System.out.println("--------------------------------------------------------------------------------------");

        for (Habit habit : habits) {
            System.out.printf("%-5d %-20s %-30s %-15s %-15s%n",
                    habit.getId(),
                    habit.getName(),
                    habit.getDescription(),
                    habit.getFrequency().toString(),
                    habit.getCreatedDate().toString());
        }
    }


    private void habitsStatManage(User user) {
        statMenu();
        statMenuChoice(user);

    }

    private void statMenu() {
        System.out.println();
        System.out.println("1. Серии выполнения");
        System.out.println("2. Процент успешного выполнения за период");
        System.out.println("3. Отчет");
        System.out.println("4. Вернуться назад");
    }

    private void statMenuChoice(User user) {

        int habitChoice = ConsoleUI.promptInt("Введите число для выбора: ");
        System.out.println();
        habitRepository.findAll(user.getId());

        switch (habitChoice) {
            case 1:
                long indexStreak = ConsoleUI.promptInt("Введите номер привычки: ") ;
                Habit habit = habitRepository.findById(indexStreak, user.getId());
                int streak = habitRepository.calculateStreak(habit);
                System.out.println("Текущая серия для привычки: " + habit.getName() + " - " + streak + " дней.");
                pauseWithEnter();
                break;
            case 2:
                showHabits(user);
                long indexHabit = ConsoleUI.promptInt("Введите номер привычки: ");
                Habit habit1 = habitRepository.findById(indexHabit, user.getId());
                LocalDate startDate = ConsoleUI.promptLocalDate("Введите начальную дату (YYYY-MM-DD): ");
                LocalDate endDate = ConsoleUI.promptLocalDate("Введите конечную дату (YYYY-MM-DD): ");
                double successRate = habitRepository.calculateSuccessRate(habit1, startDate, endDate);
                System.out.println("Процент успешного выполнения за указанный период: " + successRate + "%.");
                pauseWithEnter();
                break;
            case 3:
                generateProgressReport(user);
                pauseWithEnter();
                break;
            case 4:
                return;
            default:
                System.out.println("Неправильный выбор, попробуйте снова.");
        }
    }



    public void generateProgressReport(User user) {
        for (Habit habit : habitRepository.findAll(user.getId())) {
            System.out.println("Привычка: " + habit.getName());


            int streak = habitRepository.calculateStreak(habit);
            System.out.println("Текущая серия: " + streak);

            LocalDate startDate = LocalDate.now().minusMonths(1);
            LocalDate endDate = LocalDate.now();
            double successRate = habitRepository.calculateSuccessRate(habit, startDate, endDate);
            System.out.println("Процент выполнения за последний месяц: " + successRate + "%");


            List<ExecutionRecord> history = habitRepository.getExecutionHistoryForPeriod(habit, startDate, endDate);
            System.out.println("История выполнения:");
            for (ExecutionRecord record : history) {
                System.out.println(record.getDate() + ": " + (record.isDone() ? "Выполнено" : "Не выполнено"));
            }
            System.out.println();
        }
    }



    private Frequency chooseFrequency() {
        Frequency frequency;
        while (true) {
            System.out.println("Выберите частоту привычки: ");
            System.out.println("1. Ежедневно");
            System.out.println("2. Еженедельно");

            int choiceStatus = ConsoleUI.promptInt("Введите число для выбора: ");

            switch (choiceStatus) {
                case 1:
                    return Frequency.DAILY;
                case 2:
                    return Frequency.WEEKLY;
                default:
                    System.out.println("Неправильный выбор. Пожалуйста, выберите один из двух вариантов.");
            }
        }

    }

    public void pauseWithEnter() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Нажмите Enter, чтобы продолжить...");
        scanner.nextLine();
    }

}
