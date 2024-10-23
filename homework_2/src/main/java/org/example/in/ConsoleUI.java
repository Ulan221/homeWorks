package org.example.in;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class ConsoleUI {

    private static Scanner scanner = new Scanner(System.in);

    public static void setScanner(Scanner scanner) {
        ConsoleUI.scanner = scanner;
    }

    public static String prompt(String message) {
        System.out.print(message);
        return new Scanner(System.in).nextLine();
    }

    public static LocalDate promptLocalDate(String message) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.println(message);
                String input = scanner.nextLine();
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Неверный формат даты. Пожалуйста, введите дату в формате YYYY-MM-DD.");
            }
        }
    }

    public static int promptInt(String message) {
        while (true) {
            try {
                return Integer.parseInt(prompt(message));
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число.");
            }
        }
    }


}
