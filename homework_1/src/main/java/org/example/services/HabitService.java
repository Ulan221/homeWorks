package org.example.services;

import org.example.entities.ExecutionRecord;
import org.example.entities.Habit;
import org.example.entities.User;
import org.example.in.ConsoleUI;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HabitService {
    private final UserService userService;

    private static final String NAME_REGEX = "^[A-Za-zА-Яа-яЁё\\s]+$";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);
    private static final String INCORRECT_NAME_FORMAT_MESSAGE = "Некорректный формат названия.";

    public HabitService(UserService userService) {
        this.userService = userService;
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

    private void createHabit(User user) {
        String name = ConsoleUI.prompt("Введите название привычки: ");
        String description = ConsoleUI.prompt("Введите описание привычки: ");
        String frequency = chooseFrequency();

        if (!NAME_PATTERN.matcher(name).matches()) {
            System.out.println(INCORRECT_NAME_FORMAT_MESSAGE);
            return;
        }

        if (!NAME_PATTERN.matcher(description).matches()) {
            System.out.println(INCORRECT_NAME_FORMAT_MESSAGE);
            return;
        }

        if (frequency != null) {
            user.addHabit(name, description, frequency);
        }
    }

    private void editHabit(User user) {
        try {
            user.showHabitList();
            int indexEdit = ConsoleUI.promptInt("Введите номер привычки для редактирования: ") - 1;

            // Проверка на корректность индекса
            if (indexEdit < 0 || indexEdit >= user.getHabits().size()) {
                System.out.println("Неправильный номер привычки.");
                return;
            }

            // Запрашиваем новое название
            String newName = ConsoleUI.prompt("Введите новое название привычки: ");
            if (!NAME_PATTERN.matcher(newName).matches()) {
                System.out.println(INCORRECT_NAME_FORMAT_MESSAGE);
                return;
            }
            user.getHabitByIndex(indexEdit).setName(newName);


            String newDescription = ConsoleUI.prompt("Введите новое описание привычки: ");
            if (!NAME_PATTERN.matcher(newDescription).matches()) {
                System.out.println(INCORRECT_NAME_FORMAT_MESSAGE);
                return;
            }
            user.getHabitByIndex(indexEdit).setDescription(newDescription);


            String frequency = chooseFrequency();
            if (frequency != null) {
                user.getHabitByIndex(indexEdit).setFrequency(frequency);
                System.out.println("Привычка изменена.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Введите число.");
        }
    }


    private void markHabit(User user) {
        user.showHabitList();
        int indexToMark = ConsoleUI.promptInt("Введите номер привычки для отметки: ") - 1;
        if (indexToMark < 0 || indexToMark >= user.getHabits().size()) {
            System.out.println("Неправильный номер привычки.");
            return;
        }

        user.markHabitAsDone(user, indexToMark);
    }

    private void deleteHabit(User user) {
        user.showHabitList();
        int indexDelete = ConsoleUI.promptInt("Введите номер привычки для удаления: ") - 1;

        // Проверка на корректность индекса
        if (indexDelete < 0 || indexDelete >= user.getHabits().size()) {
            System.out.println("Неправильный номер привычки.");
            return;
        }

        user.deleteHabit(indexDelete);
        System.out.println("Привычка успешно удалена.");
    }

    private void sortAndShowHabits(User user) {
        System.out.println("Список привычек:");
        if (user.getHabits() != null && !user.getHabits().isEmpty()) {
            user.showHabitList();
            sortMenu();
            sortMenuChoice(user);
        } else {
            System.out.println("Список привычек пуст.");
        }
    }

    private void sortMenu() {
        System.out.println();
        System.out.println("1. Сортировать по имени");
        System.out.println("2. Сортировать по дате добавления");
        System.out.println("3. Вернуться назад");
    }

    private void sortMenuChoice(User user) {
        int choiceSort = ConsoleUI.promptInt("Введите число для выбора: ");
        switch (choiceSort) {
            case 1:
                Collections.sort(user.getHabits(), Habit.sortByName);
                System.out.println("Привычки отсортированы по имени.");
                break;
            case 2:
                Collections.sort(user.getHabits(), Habit.sortByDate);
                System.out.println("Привычки отсортированы по дате добавления.");
                break;
            case 3:
                return;
            default:
                System.out.println("Выберите один из двух вариантов.");
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
        user.showHabitList();

        switch (habitChoice) {
            case 1:
                int indexStreak = ConsoleUI.promptInt("Введите номер привычки: ") - 1;
                if (indexStreak >= 0 && indexStreak < user.getHabits().size()) {
                    Habit habit = user.getHabitByIndex(indexStreak);
                    int streak = calculateStreak(habit);
                    System.out.println("Текущая серия для привычки: " + habit.getName() + " - " + streak + " дней.");
                    pauseWithEnter();
                }
                break;
            case 2:
                int indexHabit = ConsoleUI.promptInt("Введите номер привычки: ") - 1;
                if (indexHabit >= 0 && indexHabit < user.getHabits().size()) {
                    Habit habit = user.getHabitByIndex(indexHabit);
                    LocalDate startDate = ConsoleUI.promptLocalDate("Введите начальную дату (YYYY-MM-DD): ");
                    LocalDate endDate = ConsoleUI.promptLocalDate("Введите конечную дату (YYYY-MM-DD): ");
                    double successRate = calculateSuccessRate(habit, startDate, endDate);
                    System.out.println("Процент успешного выполнения за указанный период: " + successRate + "%.");
                    pauseWithEnter();
                }
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


    public int calculateStreak(Habit habit) {
        List<ExecutionRecord> history = habit.getHistory();
        int streak = 0;
        LocalDate today = LocalDate.now();


        for (int i = history.size() - 1; i >= 0; i--) {
            ExecutionRecord record = history.get(i);
            if (record.isDone() && record.getDate().equals(today.minusDays(streak))) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }

    public List<ExecutionRecord> getExecutionHistoryForPeriod(Habit habit, LocalDate startDate, LocalDate endDate) {
        return habit.getHistory().stream()
                .filter(record -> !record.getDate().isBefore(startDate) && !record.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }



    public double calculateSuccessRate(Habit habit, LocalDate startDate, LocalDate endDate) {
        List<ExecutionRecord> historyForPeriod = getExecutionHistoryForPeriod(habit, startDate, endDate);
        long completedDays = historyForPeriod.stream().filter(ExecutionRecord::isDone).count();
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        return (double) completedDays / totalDays * 100;
    }


    public void generateProgressReport(User user) {
        for (Habit habit : user.getHabits()) {
            System.out.println("Привычка: " + habit.getName());

            // Показываем текущую серию
            int streak = calculateStreak(habit);
            System.out.println("Текущая серия: " + streak);


            LocalDate startDate = LocalDate.now().minusMonths(1);
            LocalDate endDate = LocalDate.now();
            double successRate = calculateSuccessRate(habit, startDate, endDate);
            System.out.println("Процент выполнения за последний месяц: " + successRate + "%");


            List<ExecutionRecord> history = getExecutionHistoryForPeriod(habit, startDate, endDate);
            System.out.println("История выполнения:");
            for (ExecutionRecord record : history) {
                System.out.println(record.getDate() + ": " + (record.isDone() ? "Выполнено" : "Не выполнено"));
            }
            System.out.println();
        }
    }


    private String chooseFrequency() {
        String frequency;
        while (true) {
            System.out.println("Выберите частоту привычки: ");
            System.out.println("1. Ежедневно");
            System.out.println("2. Еженедельно");

            int choiceStatus = ConsoleUI.promptInt("Введите число для выбора: ");

            switch (choiceStatus) {
                case 1:
                    frequency = "Ежедневно";
                    return frequency;
                case 2:
                    frequency = "Еженедельно";
                    return frequency;
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
