package org.example.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

class ExecutionRecordTest {

    @Test
    void getDate() {
        LocalDate date = LocalDate.now();
        ExecutionRecord record = new ExecutionRecord(date, true);
        assertThat(record.getDate()).isEqualTo(date);
    }

    @Test
    void isDone() {
        LocalDate date = LocalDate.now();
        ExecutionRecord record = new ExecutionRecord(date, true);
        assertThat(record.isDone()).isTrue();
    }

    @Test
    void setDate() {
        LocalDate initialDate = LocalDate.now();
        ExecutionRecord record = new ExecutionRecord(initialDate, true);

        LocalDate newDate = LocalDate.now().plusDays(1);
        record.setDate(newDate);
        assertThat(record.getDate()).isEqualTo(newDate);
    }

    @Test
    void setDone() {
        LocalDate date = LocalDate.now();
        ExecutionRecord record = new ExecutionRecord(date, true);

        record.setDone(false);
        assertThat(record.isDone()).isFalse();
    }
}
