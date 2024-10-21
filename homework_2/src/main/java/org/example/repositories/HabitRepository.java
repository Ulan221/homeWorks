package org.example.repositories;

import org.example.entities.ExecutionRecord;
import org.example.entities.Habit;

import java.time.LocalDate;
import java.util.List;

public interface HabitRepository {
    void save(Habit habit);
    void update(Habit habit);
    void delete(Long id);
    Habit findById(Long id, Long userId);
    List<Habit> findAll(Long userId);

    // Новые методы
    void markAsDone(long habitId, LocalDate date);
    int calculateStreak(Habit habit);
    double calculateSuccessRate(Habit habit, LocalDate startDate, LocalDate endDate);
    List<ExecutionRecord> getExecutionHistoryForPeriod(Habit habit, LocalDate startDate, LocalDate endDate);
}
