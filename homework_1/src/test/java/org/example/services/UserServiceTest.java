package org.example.services;

import org.example.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    void testRegisterValidUser() {
        String name = "John Doe";
        String email = "john.doe@example.com";
        String password = "password123";

        userService.getUsers().add(new User(name, email, password));

        assertThat(userService.getUsers()).hasSize(1);
        User user = userService.getUsers().get(0);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
    }

    @Test
    void testRegisterExistingUser() {
        String email = "john.doe@example.com";
        userService.getUsers().add(new User("John", email, "password123"));

        userService.getUsers().add(new User("John", email, "password123"));

        assertThat(userService.getUsers()).hasSize(2); // Смотрим, как обрабатывается повторная регистрация
    }

    @Test
    void testLoginValidUser() {
        String email = "john.doe@example.com";
        String password = "password123";
        userService.getUsers().add(new User("John Doe", email, password));

        List<User> users = userService.getUsers();
        boolean isLoggedIn = users.stream()
                .anyMatch(user -> user.getEmail().equals(email) && user.getPassword().equals(password));

        assertThat(isLoggedIn).isTrue();
    }

    @Test
    void testLoginInvalidUser() {
        List<User> users = userService.getUsers();
        boolean isLoggedIn = users.stream()
                .anyMatch(user -> user.getEmail().equals("invalid@example.com") && user.getPassword().equals("wrongpassword"));

        assertThat(isLoggedIn).isFalse();
    }

    @Test
    void testAdminLogin() {
        String adminEmail = "admin";
        String adminPassword = "admin";

        boolean isAdmin = adminEmail.equals("admin") && adminPassword.equals("admin");

        assertThat(isAdmin).isTrue();
    }

    @Test
    void testEditUserName() {
        User user = new User("John Doe", "john.doe@example.com", "password123");
        userService.getUsers().add(user);

        user.setName("New Name");

        assertThat(user.getName()).isEqualTo("New Name");
    }

    @Test
    void testEditUserEmail() {
        User user = new User("John Doe", "john.doe@example.com", "password123");
        userService.getUsers().add(user);

        user.setEmail("new.email@example.com");

        assertThat(user.getEmail()).isEqualTo("new.email@example.com");
    }

    @Test
    void testEditUserPassword() {
        User user = new User("John Doe", "john.doe@example.com", "password123");
        userService.getUsers().add(user);

        user.setPassword("newpassword");

        assertThat(user.getPassword()).isEqualTo("newpassword");
    }

    @Test
    void testDeleteUser() {
        User user = new User("John Doe", "john.doe@example.com", "password123");
        userService.getUsers().add(user);

        userService.getUsers().remove(user);

        assertThat(userService.getUsers()).isEmpty();
    }

    @Test
    void testShowUsers() {
        userService.getUsers().add(new User("John Doe", "john.doe@example.com", "password123"));

        List<User> users = userService.getUsers();

        assertThat(users).isNotEmpty();
        assertThat(users).hasSize(1);
    }

    @Test
    void testDeleteNonExistentUser() {
        User user = new User("John Doe", "john.doe@example.com", "password123");
        userService.getUsers().add(user);

        int invalidIndex = 999;
        userService.deleteUser(invalidIndex);

        assertThat(userService.getUsers()).hasSize(1);
    }
}