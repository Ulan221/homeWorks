package org.example;

import org.example.in.ConsoleUI;
import org.example.services.UserService;

public class HabitTrackerApp {
    private static UserService userService = new UserService();


    public static void main(String[] args) {
        while (true) {
            System.out.println("1. Регистрация");
            System.out.println("2. Вход");
            System.out.println("3. Выход");

            int choice = ConsoleUI.promptInt("Введите число для выбора: ");

            switch (choice) {
                case 1:
                    userService.register();
                    break;
                case 2:
                    userService.login();
                    break;
                case 3:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Неверный выбор, попробуйте снова.");
            }
        }
    }
}