package org.example.services;

import lombok.Getter;
import org.example.entities.User;
import org.example.in.ConsoleUI;
import org.example.repositories.HabitRepository;
import org.example.repositories.UserRepository;

import java.util.*;
import java.util.regex.Pattern;

public class UserService {

    private UserRepository userRepository;
    private HabitRepository habitRepository;

    @Getter
    private HabitService habitService = new HabitService(this, habitRepository);
    private static final String NAME_REGEX = "^[A-Za-zА-Яа-яЁё\\s]+$";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final String INCORRECT_NAME_FORMAT_MESSAGE = "Некорректный формат имени";
    private static final String INCORRECT_PASSSWORD_MESSAGE = "Неправильный пароль";
    private static final String USER_EXISTS_MESSAGE = "Пользователь с таким email уже существует";
    private static final String REGISTRATION_SUCCESS_MESSAGE = "Регистрация прошла успешно";
    private static final String INCORRECT_EMAIL_FORMAT_MESSAGE = "Некорректный формат email.";
    private static final String USER_NOT_FOUND_MESSAGE = "Такого пользователя нет в системе. Зарегистрируйтесь.";
    private static final String ADMIN_LOGIN_MESSAGE = "Вы вошли как администратор";
    private static final String USER_LOGIN_SUCCESS_MESSAGE = "Вы вошли в систему";

    public UserService(UserRepository userRepository, HabitRepository habitRepository) {
        this.userRepository = userRepository;
        this.habitRepository = habitRepository;
        this.habitService = new HabitService(this, habitRepository);
    }

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

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            System.out.println(USER_EXISTS_MESSAGE);
            return;
        }

        User newUser = new User(name, email, password);
        userRepository.save(newUser);
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

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                System.out.println(USER_LOGIN_SUCCESS_MESSAGE);
                showUserMenu(user);
            } else {
                System.out.println(INCORRECT_PASSSWORD_MESSAGE);
            }
        } else {
            System.out.println(USER_NOT_FOUND_MESSAGE);
        }

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
                int userIndex = ConsoleUI.promptInt("Введите номер пользователя для удаления: ");
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
        String newName = ConsoleUI.prompt("Введите новое имя: ");
        String newEmail = ConsoleUI.prompt("Введите новый email: ");
        String newPassword = ConsoleUI.prompt("Введите новый пароль: ");

        if (!NAME_PATTERN.matcher(newName).matches()) {
            System.out.println(INCORRECT_NAME_FORMAT_MESSAGE);
            return;
        }
        if (!EMAIL_PATTERN.matcher(newEmail).matches()) {
            System.out.println(INCORRECT_EMAIL_FORMAT_MESSAGE);
            return;
        }

        user.setName(newName);
        user.setEmail(newEmail);
        user.setPassword(newPassword);
        userRepository.update(user);
        System.out.println("Информация о пользователе обновлена успешно.");
    }


    private void showUsers() {
        userRepository.findAll().forEach(System.out::println);
    }

    public void deleteUser(long id) {
        userRepository.deleteById(id);
        System.out.println("Пользователь удален");
    }

    public void deleteAccount(User user) {
        userRepository.deleteById(user.getId());
        System.out.println("Ваш аккаунт удален");
    }

    public void showUserMenu(User user) {
        userMenu(user);
        userMenuChoice(user);
    }
}
