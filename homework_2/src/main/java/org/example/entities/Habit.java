package org.example.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.Frequency;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class Habit {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private Frequency frequency;
    private LocalDate createdDate;
    private List<ExecutionRecord> history;

    public Habit(String name, String description, Frequency frequency, Long userId) {
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.history = new ArrayList<>();
        this.createdDate = LocalDate.now();
        this.userId = userId;
    }

    public void markAsDone(LocalDate date) {
        for (ExecutionRecord record : history) {
            if (record.getDate().equals(date)) {
                record.setDone(true);
                return;
            }
        }
        history.add(new ExecutionRecord(date, true));
    }

    public static Comparator<Habit> sortByName = Comparator.comparing(Habit::getName);
    public static Comparator<Habit> sortByDate = Comparator.comparing(Habit::getCreatedDate);

    @Override
    public String toString() {
        return  name + ", Описание: " + description + ", Частота: " + frequency + ", Дата создания: " + createdDate;
    }
}
