package org.example;

import lombok.Getter;
import org.example.database.DatabaseConnection;
import org.example.in.ConsoleUI;
import org.example.repositories.HabitRepository;
import org.example.repositories.HabitRepositoryImpl;
import org.example.repositories.UserRepository;
import org.example.repositories.UserRepositoryImpl;
import org.example.services.UserService;

@Getter
public class HabitTrackerApp {
    private static DatabaseConnection databaseConnection = new DatabaseConnection();
    private static UserRepository userRepository = new UserRepositoryImpl(databaseConnection);
    private static HabitRepository habitRepository = new HabitRepositoryImpl(databaseConnection);
    private static UserService userService = new UserService(userRepository, habitRepository);


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