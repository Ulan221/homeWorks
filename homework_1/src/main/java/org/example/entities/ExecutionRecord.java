package org.example.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class ExecutionRecord {
    private LocalDate date;
    private boolean isDone;

    public ExecutionRecord(LocalDate date, boolean isDone) {
        this.date = date;
        this.isDone = isDone;
    }
}
