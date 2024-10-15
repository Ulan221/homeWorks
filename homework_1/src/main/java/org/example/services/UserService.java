package org.example.services;

import lombok.Getter;
import org.example.entities.User;
import org.example.in.ConsoleUI;

import java.util.*;
import java.util.regex.Pattern;

public class UserService {

    @Getter
    private List<User> users = new ArrayList<>();
    private HabitService habitService = new HabitService(this);

    private static final String NAME_REGEX = "^[A-Za-zА-Яа-яЁё\\s]+$";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final String INCORRECT_NAME_FORMAT_MESSAGE = "Некорректный формат имени";
    private static final String USER_EXISTS_MESSAGE = "Пользователь с таким email уже существует";
    private static final String REGISTRATION_SUCCESS_MESSAGE = "Регистрация прошла успешно";
    private static final String INCORRECT_EMAIL_FORMAT_MESSAGE = "Некорректный формат email.";
    private static final String USER_NOT_FOUND_MESSAGE = "Такого пользователя нет в системе. Зарегистрируйтесь.";
    private static final String ADMIN_LOGIN_MESSAGE = "Вы вошли как администратор";
    private static final String USER_LOGIN_SUCCESS_MESSAGE = "Вы вошли в систему";

    public void register() {
        String name = ConsoleUI.prompt("Введите ваше имя: ");
        String email = ConsoleUI.prompt("Введите email: ");

        if (!NAME_PATTERN.matcher(name).matches()) {
            System.out.println(INCORRECT_NAME_FORMAT_MESSAGE);
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            System.out.println(INCORRECT_EMAIL_FORMAT_MESSAGE);
            return;
        }

        String password = ConsoleUI.prompt("Введите Пароль: ");
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                System.out.println(USER_EXISTS_MESSAGE);
                return;
            }
        }

        User newUser = new User(name, email, password);
        users.add(newUser);
        System.out.println();
        System.out.println(REGISTRATION_SUCCESS_MESSAGE);
    }

    public void login() {
        String email = ConsoleUI.prompt("Введите email: ");
        String password = ConsoleUI.prompt("Введите Пароль: ");
        String adminEmail = "admin";
        String adminPassword = "admin";


        if (adminEmail.equals(email) && adminPassword.equals(password)) {
            System.out.println(ADMIN_LOGIN_MESSAGE);
            adminMenu();
            return;
        }


        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                System.out.println(USER_LOGIN_SUCCESS_MESSAGE);
                showUserMenu(user);

            }
        }

        System.out.println(USER_NOT_FOUND_MESSAGE);
    }

    private void adminMenu() {
        System.out.println();
        System.out.println("1. Получить список пользователей");
        System.out.println("2. Удалить пользователя");
        adminMenuChoice();
    }

    private void adminMenuChoice() {
        int choice = ConsoleUI.promptInt("Введите число для выбора: ");
        switch (choice) {
            case 1:
                showUsers();
                habitService.pauseWithEnter();
                break;
            case 2:
                showUsers();
                int userIndex = ConsoleUI.promptInt("Введите номер пользователя для удаления: ") - 1;
                deleteUser(userIndex);
                habitService.pauseWithEnter();
                break;
            default:
                System.out.println("Неверный выбор");
        }
    }

    private void userMenu(User user) {
        System.out.println();
        System.out.println("1. Редактирование пользователя");
        System.out.println("2. Удалить аккаунт");
        System.out.println("3. Управление привычками");
        System.out.println("4. Вернуться назад");

    }

    private void userMenuChoice(User user) {
        int choice = ConsoleUI.promptInt("Введите число для выбора: ");
        switch (choice) {
            case 1:
                editAccount(user);
                break;
            case 2:
                deleteAccount(user);
                break;
            case 3:
                habitService.habitsMenuManage(user);
                break;
            case 4:
                return;
            default:
                System.out.println("Неверный выбор");
        }
    }

    public void editAccount(User user) {
        editMenu();
        editMenuChoice(user);

    }

    private void editMenu() {
        System.out.println();
        System.out.println("1. Изменить имя");
        System.out.println("2. Изменить email");
        System.out.println("3. Изменить пароль");
        System.out.println("4. Вернуться назад");
    }

    private void editMenuChoice(User user) {
        int choice = ConsoleUI.promptInt("Введите число для выбора: ");
        switch (choice) {
            case 1:
                user.setName(ConsoleUI.prompt("Введите новое имя: "));
                System.out.println("Имя изменено");
                break;
            case 2:
                user.setEmail(ConsoleUI.prompt("Введите новый email: "));
                System.out.println("Email изменен");
                break;
            case 3:
                user.setPassword(ConsoleUI.prompt("Введите новый пароль: "));
                System.out.println("Пароль изменен");
                break;
            case 4:
                showUserMenu(user);
            default:
                System.out.println("Неверный выбор");
        }
    }

    private void showUsers() {
        System.out.println();
        for (int i = 0; i < users.size(); i++) {
            System.out.println(i + 1 + ". " + users.get(i));
        }
    }

    public void deleteUser(int index) {
        if (index >= 0 && index < users.size()) {
            users.remove(index);
            System.out.println("Пользователь удален");
        } else {
            System.out.println("Неправильно введен номер");
        }
    }

    public void deleteAccount(User user) {
        users.remove(user);
        System.out.println("Пользователь удален");
    }

    public void showUserMenu(User user) {
        userMenu(user);
        userMenuChoice(user);
    }




}
