package org.example.in;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ConsoleUITest {

    @Test
    void prompt_ShouldReturnInputString() {
        String input = "Тестовый ввод";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        String result = ConsoleUI.prompt("Введите строку: ");

        assertThat(result).isEqualTo(input);
    }

    @Test
    void promptLocalDate_ShouldReturnValidDate() {
        String input = "2024-10-11";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        LocalDate result = ConsoleUI.promptLocalDate("Введите дату: ");

        assertThat(result).isEqualTo(LocalDate.of(2024, 10, 11));
    }

    @Test
    void promptLocalDate_ShouldPromptAgainOnInvalidInput() {
        String input = "invalid-date\n2024-10-11";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        LocalDate result = ConsoleUI.promptLocalDate("Введите дату: ");

        assertThat(result).isEqualTo(LocalDate.of(2024, 10, 11));
    }

    @Test
    void promptInt_ShouldReturnValidInteger() {
        String input = "42";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        int result = ConsoleUI.promptInt("Введите число: ");

        assertThat(result).isEqualTo(42);
    }

}
