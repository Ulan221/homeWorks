package org.example.repositories;

import org.example.entities.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
        void save(User user); // Сохранение нового пользователя
        Optional<User> findByEmail(String email); // Поиск пользователя по email
        List<User> findAll(); // Получение всех пользователей
        void update(User user); // Обновление данных пользователя
        void deleteById(long id); // Удаление пользователя по id
}
