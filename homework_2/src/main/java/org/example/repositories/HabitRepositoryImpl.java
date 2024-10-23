package org.example.repositories;

import org.example.Frequency;
import org.example.database.DatabaseConnection;
import org.example.entities.ExecutionRecord;
import org.example.entities.Habit;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class HabitRepositoryImpl implements HabitRepository {
    private final DatabaseConnection databaseConnection;

    public HabitRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public void save(Habit habit) {
        String sql = "INSERT INTO habit_tracker_schema.habit (id, name, description, frequency, created_date, user_id) VALUES (nextval('habit_tracker_schema.habit_id_seq'), ?, ?, ?, ?, ?) RETURNING id";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, habit.getName());
            statement.setString(2, habit.getDescription());
            statement.setString(3, habit.getFrequency().toString());
            statement.setDate(4, Date.valueOf(habit.getCreatedDate()));
            statement.setLong(5, habit.getUserId());

            try (ResultSet generatedKeys = statement.executeQuery()) {
                if (generatedKeys.next()) {
                    habit.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Habit habit) {
        String sql = "UPDATE habit_tracker_schema.habit SET name = ?, description = ?, frequency = ? WHERE id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, habit.getName());
            statement.setString(2, habit.getDescription());
            statement.setString(3, habit.getFrequency().toString());
            statement.setLong(4, habit.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM habit_tracker_schema.habit WHERE id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Habit findById(Long id, Long userId) {
        String sql = "SELECT * FROM habit_tracker_schema.habit WHERE id = ?";
        Habit habit = null;
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                habit = mapResultSetToHabit(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return habit;
    }

    @Override
    public List<Habit> findAll(Long userId) {
        String sql = "SELECT * FROM habit_tracker_schema.habit WHERE user_id = ?";
        List<Habit> habits = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    habits.add(mapResultSetToHabit(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return habits;
    }

    private Habit mapResultSetToHabit(ResultSet resultSet) throws SQLException {
        Habit habit = new Habit();
        habit.setId(resultSet.getLong("id"));
        habit.setName(resultSet.getString("name"));
        habit.setDescription(resultSet.getString("description"));
        habit.setFrequency(Frequency.valueOf(resultSet.getString("frequency")));
        habit.setCreatedDate(resultSet.getDate("created_date").toLocalDate());
        return habit;
    }

    @Override
    public void markAsDone(long habitId, LocalDate date) {
        String sql = "INSERT INTO habit_tracker_schema.execution_record (habit_id, date, is_done) VALUES (?, ?, TRUE)";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, habitId);
            statement.setDate(2, Date.valueOf(date));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int calculateStreak(Habit habit) {
        String sql = "SELECT date, is_done FROM habit_tracker_schema.execution_record WHERE habit_id = ? ORDER BY date DESC";
        int streak = 0;
        LocalDate today = LocalDate.now();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, habit.getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                LocalDate date = resultSet.getDate("date").toLocalDate();
                boolean isDone = resultSet.getBoolean("is_done");

                if (isDone && date.equals(today.minusDays(streak))) {
                    streak++;
                } else {
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return streak;
    }

    @Override
    public double calculateSuccessRate(Habit habit, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT COUNT(*) AS completed_days FROM habit_tracker_schema.execution_record WHERE habit_id = ? AND is_done = TRUE AND date BETWEEN ? AND ?";
        long completedDays = 0;
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, habit.getId());
            statement.setDate(2, Date.valueOf(startDate));
            statement.setDate(3, Date.valueOf(endDate));

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                completedDays = resultSet.getLong("completed_days");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalDays > 0 ? (double) completedDays / totalDays * 100 : 0;
    }

    @Override
    public List<ExecutionRecord> getExecutionHistoryForPeriod(Habit habit, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT date, is_done FROM habit_tracker_schema.execution_record WHERE habit_id = ? AND date BETWEEN ? AND ?";
        List<ExecutionRecord> history = new ArrayList<>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, habit.getId());
            statement.setDate(2, Date.valueOf(startDate));
            statement.setDate(3, Date.valueOf(endDate));

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                LocalDate date = resultSet.getDate("date").toLocalDate();
                boolean isDone = resultSet.getBoolean("is_done");

                ExecutionRecord record = new ExecutionRecord(date, isDone);
                history.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return history;
    }
}
