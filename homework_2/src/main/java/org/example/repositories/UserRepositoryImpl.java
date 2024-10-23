package org.example.repositories;

import org.example.database.DatabaseConnection;
import org.example.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    private final DatabaseConnection databaseConnection;

    public UserRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO habit_tracker_schema.users (id, name, email, password) VALUES (nextval('user_id_seq'), ?, ?, ?) RETURNING id";

        try (Connection connection = databaseConnection.getConnection()) {
            if (connection == null) {
                throw new SQLException("Unable to establish a connection to the database.");
            }

            Optional<User> existingUser = findByEmail(user.getEmail());
            if (existingUser.isPresent()) {
                System.out.println("Пользователь с таким email уже существует: " + user.getEmail());
                return;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, user.getName());
                statement.setString(2, user.getEmail());
                statement.setString(3, user.getPassword());
                statement.executeUpdate();

                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }



    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM habit_tracker_schema.users WHERE email = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                User user = new User(id,
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("password"));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM habit_tracker_schema.users";
        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                User user = new User(resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("password"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Желательно заменить на логирование
        }
        return users;
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE habit_tracker_schema.users SET name = ?, email = ?, password = ? WHERE id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setLong(4, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM habit_tracker_schema.users WHERE id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Желательно заменить на логирование
        }
    }
}
