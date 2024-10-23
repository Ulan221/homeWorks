package org.example.entities;

import lombok.*;
import org.example.Frequency;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@Setter
@Getter

public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private List<Habit> habits = new ArrayList<>();

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(Long id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return "Пользователь: " + name + ", email: " + email;
    }
}
