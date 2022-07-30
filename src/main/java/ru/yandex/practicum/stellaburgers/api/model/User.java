package ru.yandex.practicum.stellaburgers.api.model;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Locale;

public class User {
    private String email;
    private String name;
    private String password;

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public static User getRandomUser() {
        String name = RandomStringUtils.randomAlphabetic(10);
        String password = name;
        String email = name.toLowerCase(Locale.ROOT) + "@yandex.ru";
        return new User(email, password, name);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
