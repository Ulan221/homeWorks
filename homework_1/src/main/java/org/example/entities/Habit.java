package org.example.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;



@Setter
@Getter
@NoArgsConstructor

public class Habit {
    private String name;
    private String description;
    private String frequency;
    private LocalDate createdDate;
    private List<ExecutionRecord> history;

    public Habit(String name, String description, String frequency, LocalDate now) {
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.history = new ArrayList<>();
        this.createdDate = now;
    }

    public void markAsDone(LocalDate date) {
        for (ExecutionRecord record : history) {
            if (record.getDate().equals(date)) {
                record.setDone(true);
                return;
            }
        }

        ExecutionRecord newRecord = new ExecutionRecord(date, true);
        history.add(newRecord);
    }

    public static Comparator<Habit> sortByName = new Comparator<Habit>() {
        @Override
        public int compare(Habit h1, Habit h2) {
            return h1.getName().compareTo(h2.getName());
        }
    };

    public static Comparator<Habit> sortByDate = new Comparator<Habit>() {
        @Override
        public int compare(Habit h1, Habit h2) {
            return h1.getCreatedDate().compareTo(h2.getCreatedDate());
        }
    };

    @Override
    public String toString() {
        return "Привычка: " + name + ", Описание: " + description + ", Частота: " + frequency;

    }
}
