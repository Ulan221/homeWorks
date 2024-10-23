package org.example.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class ExecutionRecordTest {

    @Test
    public void testConstructorAndGetters() {
        LocalDate testDate = LocalDate.of(2024, 10, 23);
        boolean isDone = true;

        ExecutionRecord executionRecord = new ExecutionRecord(testDate, isDone);

        Assertions.assertEquals(testDate, executionRecord.getDate(), "Дата должна совпадать с заданной.");
        Assertions.assertTrue(executionRecord.isDone(), "Статус выполнения должен быть true.");
    }

    @Test
    public void testSetters() {
        LocalDate testDate = LocalDate.of(2024, 10, 23);
        ExecutionRecord executionRecord = new ExecutionRecord(testDate, false);

        LocalDate newDate = LocalDate.of(2024, 10, 24);
        executionRecord.setDate(newDate);
        executionRecord.setDone(true);

        Assertions.assertEquals(newDate, executionRecord.getDate(), "Дата должна быть обновлена.");
        Assertions.assertTrue(executionRecord.isDone(), "Статус выполнения должен быть обновлен на true.");
    }

    @Test
    public void testDefaultState() {
        ExecutionRecord executionRecord = new ExecutionRecord(LocalDate.now(), false);

        Assertions.assertNotNull(executionRecord.getDate(), "Дата не должна быть null.");
        Assertions.assertFalse(executionRecord.isDone(), "Статус выполнения по умолчанию должен быть false.");
    }
}
